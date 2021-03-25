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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

// DELETE THIS
public class NoOp implements Instruction {
    private String vClass;
    private String comment;
    private Vertex src;

    NoOp(GraphTraversalSource g, Vertex v, String vClass) {
        this.src = v;
        this.vClass = vClass;
        if(vClass.equals("ParserStateContext")){
            String stateName = (String)
                g.V(v).outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                    .inV()
                    .values("value")
                    .next();
            comment = "state "+ stateName;
        } 

        if(vClass.equals("BlockStatementContext")){
            comment = "start of block";
        }
    }

    @Override
    public String toString() {
        return "NoOp [vClass=" + vClass + "]";
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment(comment));
        return insts;
    }

    @Override
    public Vertex getOrigin() {
        return src;
    }

}
