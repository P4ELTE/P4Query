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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.Struct;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Inc;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Sub;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.Size;
import p4query.ontology.Dom;

public class LiteralExpression implements Expression {
    enum LiteralType { INTEGER, BOOLEAN }

    // TODO make this an enum
    private final String value;
    private final LiteralType type;
    CustomStorageReference storage;
    private IRType typeHint;

    public LiteralExpression(CompilerState state, Integer v, int sizeHint) {
        this.value = Integer.toString(v);
        this.type = LiteralType.INTEGER;


        this.typeHint = state.getTypeFactory().create("INTEGER_" + sizeHint, sizeHint, this);
        String globalName = state.getParentDecl().addTemporary(typeHint);
        this.storage = new CustomStorageReference(state, "custom value", globalName, typeHint, this);
    }

    public LiteralExpression(CompilerState state, Vertex v, IRType typeHint) throws UnableToParseException {
        GraphTraversalSource g = state.getG();

        if (g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "INTEGER").hasNext()){

            this.value = (String)
                g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "INTEGER")
                    .inV()
                    .values("value")
                    .next();
            this.type = LiteralType.INTEGER;

        } else if (g.V(v).outE(Dom.SYN).or(__.has(Dom.Syn.E.RULE, "TRUE")).hasNext()) {
            this.value = "1";
            this.type = LiteralType.BOOLEAN;

        } else {
            throw new UnableToParseException(LiteralExpression.class, v);
        }

//        if(sizeHint == -1){
//            // TODO the reason this is an error, because integer can be of any size. for example, bit<48> is an integer.
////            throw new IllegalArgumentException("Cannot instantiate literal with unknown size (type " + this.type + ", value " + this.value + ", vertex " + v);
//            sizeHint = 1;
//        }

//        IRType type = typeFactory.create(this.type + "_" + sizeHint, sizeHint, this);
        String globalName = state.getParentDecl().addTemporary(typeHint);
        this.storage = new CustomStorageReference(state, "TerminalNodeImpl", globalName, typeHint, this);
        this.typeHint = typeHint;
    }


    @Override
    public String toString() {
        return "LiteralExpression [type=" + typeHint + ", value=" + value + "]";
    }

    @Override
    public String toP4Syntax() {
        return value;
    }

    @Override
    public StorageReference getStorageReference() {
        return storage;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        // TODO this should be: put all bits (incl zero-padding) on the stack, dereference, then memcpy, then pop all bits from the stack. 

        int w = value.indexOf("w"); // TODO use this as a type hint
        String val2 = w == -1 ? value : value.substring(w + 1);
        int x = val2.indexOf("x"); 
        Long n = x == -1 ? Long.parseLong(val2) : Long.parseLong(val2.substring(x + 1), 16);

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment(value));

        if(typeHint.getSize() > 1){
            int fullSize = typeHint.getSize();
            int size = fullSize;
            if(typeHint instanceof Struct && ((Struct) typeHint).isP4Header()){
                size -= 2;
            }

            String bs  = Long.toBinaryString(n);
            int nSize = bs.length();

            if(nSize > size) 
               throw new IllegalStateException(
                   String.format("%d bits were allocated for literal %s, but it requires %d bits", size, value, nSize));

            char[] bits = bs.toCharArray() ;
                
            for (int i = bits.length -1; i >= 0 ; i--) {
               insts.add(new Const(new Int(bits[i] - '0', i + "th bit of " + value)));

               insts.addAll(this.storage.compileToLIR(local, global)); 
               insts.add(new Const(new Size(fullSize - (bits.length-1 - i) - 1, "suffix of " + typeHint.getName()))); // end of dst addr
               insts.add(new Add());

               insts.add(new PutField());
            }

            insts.addAll(this.storage.compileToLIR(local, global)); 
        } else {
            insts.add(new Const(new Int(n, typeHint.getName())));
            insts.addAll(this.storage.compileToLIR(local, global));
            insts.add(new PutField());
            insts.addAll(this.storage.compileToLIR(local, global));
        }

        insts.add(new Comment("end of " + value));
        return insts;    
    }

    @Override
    public IRType getTypeHint() {
        return typeHint;
    }
}
