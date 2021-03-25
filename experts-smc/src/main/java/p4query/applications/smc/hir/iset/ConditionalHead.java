/**
 * Copyright 2020-2021, Eötvös Loránd University.
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
 */
package p4query.applications.smc.hir.iset;

import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.exprs.CustomStorageReference;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.UnresolvedVertexLabel;
import p4query.ontology.Dom;

public class ConditionalHead implements Instruction {

    private CustomStorageReference boolStore;
    private Expression head;
    private Vertex src;

    public ConditionalHead(GraphTraversalSource g, Vertex v, String vClass,
			IRType.SingletonFactory typeFactory, ProcedureDefinition procDef) {

        this.src = v;

        Vertex cond = g.V(v).outE(Dom.SYN)
                            .has(Dom.Syn.E.RULE, "expression").inV()
                            .next();

        this.head = Expression.Factory.create(g, cond, typeFactory, procDef, 1);

//      no need for extra store, functions should just push the boolean on the stack
//        IRType type = typeFactory.create("BOOLEAN", 1, head);
//        String localName = procDef.addTemporary(type);
//        CustomStorageReference result = new CustomStorageReference(vClass, localName, type, head);
//        this.boolStore = result;
	}

	@Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("if(" + head.toP4Syntax() + ")"));
        insts.addAll(head.compileToLIR(local, global));
//         insts.addAll(boolStore.compileToLIR(local, global));
        insts.add(new IfEq(new UnresolvedVertexLabel(src, "jump if not " + head.toP4Syntax())));

        return insts;
    }

    @Override
    public Vertex getOrigin() {
        return src;
    }
}
