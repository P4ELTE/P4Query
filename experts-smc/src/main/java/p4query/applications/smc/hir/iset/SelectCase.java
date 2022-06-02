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
package p4query.applications.smc.hir.iset;

import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.exprs.SelectKeyExpression;
import p4query.applications.smc.hir.p4api.ProcedureDefinition;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

public class SelectCase implements Instruction {
    private Vertex src;
    private SelectKeyExpression expr; 

    SelectCase(CompilerState state, Vertex v, String vClass) {
        this.src = v;

        Vertex selectExpr = 
            state.getG().V(v).repeat(__.inE(Dom.SYN).outV()) 
                .until(__.has(Dom.Syn.V.CLASS, "SelectExpressionContext"))
                .next();

        Expression headExpr = SelectHead.findSelectHeadExpression(state, selectExpr);

        this.expr = (SelectKeyExpression) Expression.Factory.create(state, src, headExpr.getTypeHint());

    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.addAll(expr.compileToLIR(local, global));

        return insts;
    }



    @Override
    public Vertex getOrigin() {
        return src;
    }

    
}
