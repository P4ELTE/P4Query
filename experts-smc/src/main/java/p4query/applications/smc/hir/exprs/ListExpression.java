/**
 * Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Dániel Lukács, 2022
 */
package p4query.applications.smc.hir.exprs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.DerefTop;
import p4query.applications.smc.lir.iset.Inc;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.PopN;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Sub;
import p4query.applications.smc.lir.iset.Not;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class ListExpression implements Expression {

    private List<Expression> elems = new LinkedList<>();
    private CustomStorageReference storage;
    private IRType typeHint;

    public ListExpression(CompilerState state, List<Expression> elems, IRType typeHint) {
        this.elems = elems;
        IRType type = state.getTypeFactory().create("LIST_" + this.elems.size(), this.elems.size(), this);
        String localName = state.getParentDecl().addTemporary(type);
        this.storage = new CustomStorageReference(state, "ExpressionListContext", localName, type, this);
        this.typeHint = type;
    }

    public ListExpression(CompilerState state, Vertex v, IRType typeHint) throws UnableToParseException {
        GraphTraversalSource g = state.getG();

        if (!g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").hasNext()) {
            throw new UnableToParseException(ListExpression.class, v);
        }

        List<Vertex> elems0 = 
            g.V(v)
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV())
            .emit()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
            .toList();
        
        Collections.reverse(elems0);

        // we allow non-composite types, namely gentypes. 
//        if(!(typeHint instanceof Composite)){
//            throw new IllegalArgumentException(String.format("Type hint class was expected to subclass %s, got class %s instead.", Composite.class, typeHint.getClass()));
//        }

        List<IRType> hintFields;
        if(typeHint != null && typeHint instanceof Composite){ 
            hintFields = new ArrayList<>(((Composite) typeHint).getFields().values());
        } else {
            // the type hint is not a composite. (it can happen when the callee has a generic type parameter, or when the callee was defined manually.) just create a list of nulls, so that the following code won't break.
            hintFields = Arrays.asList(new IRType[elems0.size()]);
        }

        if(hintFields.size() != elems0.size()){
            throw new IllegalStateException(
                String.format("Type error: List has %s elements, while type hint has %s fields. (If this is a header, you need to include validity bit and size as well.) List vertex: %s. Type hint: %s.", elems0.size(), hintFields.size(), g.V(v).elementMap().next(), hintFields ));
        }

        int i = 0;
        for (Vertex elem : elems0) {
            elems.add(Expression.Factory.create(state, elem, hintFields.get(i)));
            i += 1;
        }

        IRType type = state.getTypeFactory().create("LIST_" + elems.size(), elems.size(), this);
        String localName = state.getParentDecl().addTemporary(type);
        this.storage = new CustomStorageReference(state, "ExpressionListContext", localName, type, this);
        this.typeHint = type;
    }

    @Override
    public String toP4Syntax() {
        return  "{" +
                String.join(",", elems.stream()
                                     .map(e -> e.toP4Syntax())
                                     .toArray(String[]::new)) +
                "}";
    }

    @Override
    public StorageReference getStorageReference() {
        return storage;
    }

    public  List<Expression> getElems(){
        return this.elems;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment(this.toP4Syntax()));

        // process all the elements to the stack. this will push elems.size() many pointers pointing to the values.
        for (Expression elem : elems) {
            insts.addAll(elem.compileToLIR(local, global));
        }
        insts.add(new Comment("memcpy(src,dst,length)"));

        // src: get the address of the first list element. (1st element is nth - n + 1)
        insts.add(new DerefTop());
        insts.add(new Const(new Size(elems.size(), "list")));
        insts.add(new Sub());
        insts.add(new Inc());

        // dst: address of the list
        insts.addAll(this.storage.compileToLIR(local, global));

        // length
        insts.add(new Const(new Size(elems.size(), "list")));

        // memcpy(src,dst,length)
        insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcpy", ""), new Size(3, "src, dst, length")));

        // cleanup memcpy
        insts.add(new Pop());
        insts.add(new Comment("end of memcpy(src,dst,length)"));

        // cleanup list elements 
        insts.add(new PopN(new Size(elems.size(), "list")));

        // push result: the address of the list
        insts.addAll(this.storage.compileToLIR(local, global));

        insts.add(new Comment("end of " + this.toP4Syntax()));
        return insts;
    }

    @Override
    public IRType getTypeHint() {
        return typeHint;
    }

    
}
