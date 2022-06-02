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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.Struct;
import p4query.applications.smc.hir.typing.IRType.SingletonFactory;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Not;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class BinaryExpression implements Expression {
    // TODO make this an enum
    private final String operator;
    private List<Expression> operands = new LinkedList<>();
    private StorageReference storage;
    private IRType typeHint;
    private CompilerState state;

    BinaryExpression(CompilerState state, Vertex src, IRType typeHint) throws UnableToParseException {
        this.typeHint = typeHint;
        this.state = state;

        GraphTraversalSource g = state.getG();

        try {
            this.operator = (String) g.V(src).outE(Dom.SYN)
                    .or(__.has(Dom.Syn.E.RULE, "MINUS"), 
                        __.has(Dom.Syn.E.RULE, "PLUS"),
                        __.has(Dom.Syn.E.RULE, "EQ"),
                        __.has(Dom.Syn.E.RULE, "NE")
                        ).values(Dom.Syn.E.RULE).next();
        } catch (NoSuchElementException e) {
            throw new UnableToParseException(BinaryExpression.class, src);
        }

        List<Vertex> opds = g.V(src).outE(Dom.SYN).not(__.has(Dom.Syn.E.RULE, this.operator)).order()
                .by(Dom.Syn.E.ORD, Order.asc).inV().toList();

        // Binary operations work with operands of the same size.
        if(opds.size() != 2){ 
            throw new IllegalStateException("Binary operator with less than 2 operands found.");
        }

        // TODO operands can be in any order, there is no guarantee the left one (or any) will have a typehint
        Expression left = Expression.Factory.create(state, opds.get(0), typeHint); 
        Expression right = Expression.Factory.create(state, opds.get(1), left.getTypeHint());

        operands.add(left);
        operands.add(right);

        allocateStorageReference(typeHint);
    }

    public String getOperator() {
        return operator;
    }

    public List<Expression> getOperands() {
        return operands;
    }

    @Override
    public String toP4Syntax() {
        String opString = null;
        switch(this.getOperator()){
            case "MINUS": opString = "-"; break;
            case "PLUS": opString = "+"; break;
            case "EQ": opString = "=="; break;
            case "NE": opString = "!="; break;
            default:
                throw new IllegalArgumentException("Unknown binary expression operator " + this.getOperator());
        }
        return this.getOperands().get(0).toP4Syntax() + opString +  this.getOperands().get(1).toP4Syntax();
    }

    public void allocateStorageReference(IRType typeHint) {

        /*
        // note: this sets leftSize and rightSize, but they are never used
        Expression left = this.getOperands().get(0);
        Expression right = this.getOperands().get(1);

        Integer leftSize = null;
        Integer rightSize = null;
        if (left instanceof P4StorageReference) {
            leftSize = ((P4StorageReference) left).getSizeHint();
        }
        if (right instanceof P4StorageReference) {
            rightSize = ((P4StorageReference) left).getSizeHint();
        }

        if (leftSize != null && rightSize != null && !leftSize.equals(rightSize)) {
            throw new IllegalStateException(
                    String.format("Left opd %s and right opd %s has different sizes", left, right));
        } else if (leftSize != null) {
            rightSize = leftSize;
        } else if (rightSize != null) {
            leftSize = rightSize;
        } else { // both null
            rightSize = sizeHint;
            leftSize = sizeHint;
        }
        */
      // int sizeHint = typeHint.getSize();
      //  IRType type = typeFactory.create("INTEGER_" + sizeHint, sizeHint, this);
        String globalName = state.getParentDecl().addTemporary(typeHint);
        CustomStorageReference result = new CustomStorageReference(state, "binary-expr", globalName, typeHint,
                this);

        this.storage = result;
    }

    @Override
    public StorageReference getStorageReference() {
        return storage;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();

        Expression left = this.getOperands().get(0);
        Expression right = this.getOperands().get(1);

        insts.add(new Comment(this.toP4Syntax() + " (size: " + typeHint.getSize() + ")"));


        insts.addAll(left.compileToLIR(local, global));
        // memcmp wants actual addresses, not list pointers. find out the absolute address of the first field, this is also the absolute address of the whole struct
        // TODO p4c forbids using headers as values. is resolveListPointers really needed? (if yes, we should also account for handling validity and size fields of headers)
//        insts.addAll(resolveListPointers(left));

        insts.addAll(right.compileToLIR(local, global));

//        insts.addAll(resolveListPointers(right));

        insts.add(new Const(new Size(left.getTypeHint().getSize(), left.toP4Syntax())));

        switch(this.operator){
            case "MINUS":
                insts.addAll(this.storage.compileToLIR(local, global));
                insts.add(new Invoke(new UnresolvedNameLabel("stdlib","subtract",""), new Size(4, "left, right, target, length")));
                // subtract works in-place, and leaves target address on the stack
                break;
            case "EQ":
                insts.add(new Invoke(new UnresolvedNameLabel("stdlib","memcmp",""), new Size(3, "left, right, length")));
                break;
            case "NE":
                insts.add(new Invoke(new UnresolvedNameLabel("stdlib","memcmp",""), new Size(3, "left, right, length")));
                insts.add(new Not());
                break;
        }


        insts.add(new Comment("end of " + this.toP4Syntax() + " (size: " + typeHint.getSize() + ")"));

        return insts;

    }

    // TODO this is probably needed at other places as well
    private LinkedList<StackInstruction> resolveListPointers(Expression expr) {
        if(expr instanceof StorageReference){
           return resolveListPointers(expr.getTypeHint());
        } 
        return new LinkedList<>();
    }

    private LinkedList<StackInstruction> resolveListPointers(IRType ref) {
        LinkedList<StackInstruction> insts = new LinkedList<>();

        if(ref instanceof Struct){
            Struct sref = (Struct) ref;
            if(sref.isP4Struct() || sref.isP4Header()){
                insts.add(new GetField());
                if(sref.isP4Header()){
                    insts.add(new Const(new Int(2, "offset: validity, size")));
                    insts.add(new Add());
                } else {
                    Iterator<IRType> child  = sref.getFields().values().iterator();
                    if(child.hasNext())
                        insts.addAll(resolveListPointers(child.next()));
                }
            }
        }
        return insts;

    }

    @Override
    public IRType getTypeHint() {
        return typeHint;
    }

    
}
