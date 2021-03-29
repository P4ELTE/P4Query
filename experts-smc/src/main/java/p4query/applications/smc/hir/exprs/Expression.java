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
package p4query.applications.smc.hir.exprs;

import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

public interface Expression {

    public String toP4Syntax();

    public StorageReference getStorageReference();

    // compileToLIR is expected to push (or load) the value (or address) of the expression on the stack.  
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global );
    public int getSizeHint();

    public static class Factory {
        private Factory() {
        }

        public static Expression create(GraphTraversalSource g, Vertex v, IRType.SingletonFactory typeFactory, Definition parentDef, int sizeHint) {
            if (g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").hasNext()) {
                System.err.println("Warning: Expression lists are not implemented");
                return null;
            }

            if (g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression").hasNext()){
                return new SelectKeyExpression(g, v, typeFactory, parentDef);
            }

            if (g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").hasNext())
                return new ProcedureCallExpression(g, v, typeFactory, parentDef);

            if (g.V(v).outE(Dom.SYN)
                    .or(__.has(Dom.Syn.E.RULE, "nonTypeName"), __.has(Dom.Syn.E.RULE, "prefixedNonTypeName")).hasNext())
                return new P4StorageReference(g, v, typeFactory);

            if (g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "DOT").hasNext()) {
                return handleDot(g, v, typeFactory);
            }

            if (g.V(v).outE(Dom.SYN).or(__.has(Dom.Syn.E.RULE, "MINUS"), __.has(Dom.Syn.E.RULE, "PLUS")).hasNext())
                return new ArithmeticExpression(g, v, typeFactory, parentDef, sizeHint);

            if (g.V(v).outE(Dom.SYN).or(__.has(Dom.Syn.E.RULE, "INTEGER")).hasNext())
                return new LiteralExpression(g, v, typeFactory, parentDef, sizeHint);

            System.err.println(g.V(v).elementMap().next());
            throw new IllegalArgumentException(String.format("Cannot create expression from vertex %s", v));
        }

        private static Expression handleDot(GraphTraversalSource g, Vertex v, IRType.SingletonFactory typeFactory) {

            Vertex leftMostField = 
                g.V(v).repeat(__.outE(Dom.SYN)
                                .or(__.has(Dom.Syn.E.RULE, "expression"),
                                    __.has(Dom.Syn.E.RULE, "lvalue"))
                                .inV())
                      .until(__.not(__.outE(Dom.SYN)
                                      .has(Dom.Syn.E.RULE, "DOT")))

                      .next();

            String type = (String)
                g.V(leftMostField)
                 .repeat(__.outE(Dom.SYN).inV())
                 .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                 .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                 .outV()
                 // note: optional will run for StructTypeDecl and HeaderTypeDecl. it will not run for EnumDeclaration
                 .optional( 
                    __.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                    .inV()
                    .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                    .outV())
                 .values(Dom.Syn.V.CLASS)
                 .next();

            if(type.equals("StructTypeDeclarationContext")){
                return new P4StorageReference(g, v, typeFactory);
            } else if(type.equals("HeaderTypeDeclarationContext")){
                return new P4StorageReference(g, v, typeFactory);
            } else if (type.equals("EnumDeclarationContext")){
                System.err.println("Warning: enum expression encountered. Ignoring.");
                return null;
            } else {
                System.err.println(g.V(v).elementMap().next());
                System.err.println(type);
                System.err.println(g.V(leftMostField).elementMap().next());
                throw new IllegalArgumentException("Cannot handle dot expression on vertex " + v);
            }

        }
    }
    
}
