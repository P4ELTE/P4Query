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

import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

public interface Expression {

    public String toP4Syntax();

    public StorageReference getStorageReference();

    // compileToLIR is expected to push (or load) the value (or address) of the expression on the stack.  
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global );
//     public int getSizeHint();
    public IRType getTypeHint();

    public static class Factory {
        private Factory() {
        }

        public static Expression createLvalue(CompilerState state, Vertex v) {
            GraphTraversalSource g = state.getG();

            for (int i = 0; i < 2; i++) {
                try {
                    switch(i){
                        case 0: return new P4StorageReference(state, v);
                        case 1: return handleDot(state, v);
                    }
                } catch(UnableToParseException e){
                    continue;
                }
            }

            throw new IllegalArgumentException(String.format("Cannot create lvalue expression from vertex %s (%s)", v, g.V(v).elementMap().next()));
        }

        public static Expression create(CompilerState state, Vertex v, IRType typeHint) {

            GraphTraversalSource g = state.getG();

            for (int i = 0; i < 9; i++) {
                try {
                    switch(i){
                        case 0:
                            return new ListExpression(state, v, typeHint);
                        case 1:
                            return new SelectKeyExpression(state, v, typeHint);
                        case 2:
                            return new ProcedureCallExpression(state, v);
                        case 3:
                            return new P4StorageReference(state, v);
                        case 4:
                            return handleDot(state, v);
                        case 5:
                            return new BinaryExpression(state, v, typeHint);
                        case 6:
                            return new UnaryExpression(state, v, typeHint);
                        case 7:
                            return new LiteralExpression(state, v, typeHint);
                        case 8:
                            return new BitSliceExpression(state, v, typeHint);
                    }
                } catch(UnableToParseException e){
                    continue;
                }
            }

            System.err.println(g.V(v).elementMap().next());
            throw new IllegalArgumentException(String.format("Cannot create expression from vertex %s (%s)", v, g.V(v).elementMap().next()));
        }

        // static dispatch
        private static Expression handleDot(CompilerState state, Vertex v) throws UnableToParseException {

            GraphTraversalSource g = state.getG();

            if (!g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "DOT").hasNext()) {
                throw new UnableToParseException(P4StorageReference.class, v);
            }

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
                return new P4StorageReference(state, v, true);
            } else if(type.equals("HeaderTypeDeclarationContext")){
                return new P4StorageReference(state, v, true);
            } else if(type.equals("TableDeclarationContext")){
                return new InlineTableApplication(state, v);
            } else if (type.equals("EnumDeclarationContext")){
                return new EnumExpression(state, v);
            } else {
                System.err.println(g.V(v).elementMap().next());
                System.err.println(type);
                System.err.println(g.V(leftMostField).elementMap().next());
                throw new IllegalArgumentException("Cannot handle dot expression on vertex " + v);
            }

        }
    }
    
}
