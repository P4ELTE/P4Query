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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.ontology.Dom;

public class BitSliceExpression implements Expression {
    CustomStorageReference storage;
    private IRType typeHint;
    private Integer value = 1;

    public BitSliceExpression(CompilerState state, Vertex src, IRType typeHint) throws UnableToParseException {
        GraphTraversalSource g = state.getG();

        if(!g.V(src)
             .filter(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "COLON"))
             .filter(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "L_BRACKET"))
             .filter(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "R_BRACKET"))
             .hasNext()){
            throw new UnableToParseException(BitSliceExpression.class, src);
        }

        System.err.println("Warning: bit-slice expression encountered. Using dummy value.");
        this.typeHint = new GenType("bit-slice-type", 1, null);
        String globalName = state.getParentDecl().addTemporary(typeHint);
        this.storage = new CustomStorageReference(state, "TerminalNodeImpl", globalName, typeHint, this);
    }

    @Override
    public String toString() {
        return "BitSliceExpression [value=" + value + "]";
    }

    @Override
    public String toP4Syntax() {
        return value.toString();
    }

    @Override
    public StorageReference getStorageReference() {
        return storage;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("enum field"));
        insts.add(new Const(new Int(value, typeHint.getName())));
        insts.addAll(this.storage.compileToLIR(local, global));
        insts.add(new PutField());
        insts.addAll(this.storage.compileToLIR(local, global));

        insts.add(new Comment("end of field"));

        return insts;    
    }

    @Override
    public IRType getTypeHint() {
        return typeHint;
    }
    
}
