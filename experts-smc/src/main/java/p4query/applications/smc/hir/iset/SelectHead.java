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
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;


public class SelectHead implements Instruction {
    private ProcedureDefinition procDef;
    private IRType.SingletonFactory typeFactory;
    private Vertex src;
    private GraphTraversalSource g;
    private Expression expr;

    SelectHead(GraphTraversalSource g, Vertex selectExpr, String vClass, IRType.SingletonFactory typeFactory,
            ProcedureDefinition procDef) {
        this.g = g;
        this.src = selectExpr;
        this.typeFactory = typeFactory;
        this.procDef = procDef;

        this.expr = findSelectHeadExpression(g, selectExpr, typeFactory, procDef);
    }

    public static Expression findSelectHeadExpression(GraphTraversalSource g, Vertex selectExpr, IRType.SingletonFactory typeFactory, Definition procDef2){

        Vertex head;
        try { 
            head = 
                g.V(selectExpr)
                    .outE(Dom.SYN)
                    .has(Dom.Syn.E.RULE, "expressionList").inV()
                    .outE(Dom.SYN)
                    .has(Dom.Syn.E.RULE, "expression")
                    .inV()
                    .next();
        } catch(NoSuchElementException e){
            throw new IllegalArgumentException(
                String.format("Cannot find head of select expression %s. Maybe the head expression is expression list? (not implemented)"));
        }
        
        // TODO this should be a default literal size (that should be ignored if the head is not a literal)
        return Expression.Factory.create(g, head, typeFactory, procDef2, -1);
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
