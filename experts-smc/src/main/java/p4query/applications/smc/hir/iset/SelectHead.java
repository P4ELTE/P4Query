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
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.ProcedureDefinition;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;


public class SelectHead implements Instruction {
    private Vertex src;
    private Expression expr;

    SelectHead(CompilerState state, Vertex selectExpr, String vClass) {
        this.src = selectExpr;

        this.expr = findSelectHeadExpression(state, selectExpr);
    }

    public static Expression findSelectHeadExpression(CompilerState state, Vertex selectExpr){

        GraphTraversalSource g = state.getG();
        try { 
            g.V(selectExpr)
                .has(Dom.Syn.V.CLASS, "SelectExpressionContext")
                .outE(Dom.SYN)
                .has(Dom.Syn.E.RULE, "expressionList").inV()
                .next();
            
        } catch(NoSuchElementException e){
            throw new IllegalArgumentException(
                String.format("Cannot find head of select expression %s.", g.V(selectExpr).elementMap().next()));
        }
        
        // NOTE: The spec does not require select heads to be lvalues, but we do.
        //       This is because otherwise both the head and the patterns will be
        //       literals, and we won't be able to tell their size (unless there is a default). 
        //       Of the two, heads are more likely to be lvalues: it makes sense to pattern match lvalues to literal patterns, but not the other way (e.g. JAVA does not allow that either). 
      //  return Expression.Factory.createLvalue(state, head);
        return Expression.Factory.create(state, selectExpr, null);
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("select " + expr.toP4Syntax()));
        insts.addAll(expr.compileToLIR(local, global));

        return insts;
    }

    @Override
    public Vertex getOrigin() {
        return src;
    }
    
}
