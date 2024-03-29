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
package p4query.experts.callsites;

import java.util.List;

import javax.inject.Singleton;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.codejargon.feather.Provides;

import p4query.ontology.Dom;
import p4query.ontology.Status;
import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.CallSites;
import p4query.ontology.analyses.SymbolTable;
import p4query.ontology.analyses.SyntaxTree;

public class CallSitesImpl {

    // NOTE P4 spec has almost nothing about type instantiations and method dispatch mechanisms.
    //      It is not clear whether packet.extract(...) refers to the extract method in the 'packet' namespace, where 'packet' is just an alias to 'packet_in', or
    //      it is actually a method call extract(packet, ...), where the definition of extract is selected based on the static type of 'packet'.
    //      The first case is simpler, so I went with this for now.
        // TODO make this work for other kind of calls and functions
        @Provides
        @Singleton
        @CallSites
        public Status analyse(GraphTraversalSource g, @SyntaxTree Status s, @AbstractSyntaxTree Status a, @SymbolTable Status t){

            long startTime = System.currentTimeMillis();
            whichCallInvokesWhichFunction(g);
            whichCallOwnsWhichArguments(g);
            whichFunctionOwnsWhichParameters(g);
            whichArgumentsInstantiateWhichParameters(g);

            fixParserParameters(g); 

            long stopTime = System.currentTimeMillis();
            System.out.println(String.format("%s complete. Time used: %s ms.", CallSites.class.getSimpleName() , stopTime - startTime));
            return new Status();
        }
        
        private static void whichCallInvokesWhichFunction(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN)
            .or(__.has(Dom.Syn.V.CLASS, "FunctionPrototypeContext"),
                __.has(Dom.Syn.V.CLASS, "TableDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "PackageTypeDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))
            .as("decl")
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).inV()
            .optional( 
                __.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).outV()
                  .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).inV())
            .repeat(__.in(Dom.SYN))
            .until(
                __.or(__.has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext")
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList"),
                      __.has(Dom.Syn.V.CLASS, "DirectApplicationContext"),
                      __.has(Dom.Syn.V.CLASS, "InstantiationContext"),
                      __.has(Dom.Syn.V.CLASS, "ExpressionContext")
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList")))
            .addE(Dom.SITES).to("decl")
            .property(Dom.Sites.ROLE, Dom.Sites.Role.CALLS)
            
            .iterate();

        }

        private static void whichCallOwnsWhichArguments(GraphTraversalSource g) {
            // TODO couldn't check but the arguments are probably in reverse order
            g.V().hasLabel(Dom.SYN)
            .or(__.has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext"),
                __.has(Dom.Syn.V.CLASS, "InstantiationContext")).as("call")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").inV()
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonEmptyArgList").inV())
            .emit()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argument").inV()
            .outE(Dom.SYN).inV()
            .addE(Dom.SITES).from("call")
            .property(Dom.Sites.ROLE, Dom.Sites.Role.HAS_ARGUMENT)
            
            .iterate();
        }
        private static void whichFunctionOwnsWhichParameters(GraphTraversalSource g) {
            // TODO couldn't check but the parameters are probably in reverse order
            g.V().or(__.has(Dom.Syn.V.CLASS, "FunctionPrototypeContext"),
                     __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"),
                     __.has(Dom.Syn.V.CLASS, "ControlTypeDeclarationContext"),
                     __.has(Dom.Syn.V.CLASS, "PackageTypeDeclarationContext")).as("func")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parameterList").inV()
             .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonEmptyParameterList").inV())
             .emit()
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parameter").inV()
//             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
//             .repeat(__.out(Dom.SYN))
//             .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .addE(Dom.SITES).from("func")
             .property(Dom.Sites.ROLE, Dom.Sites.Role.HAS_PARAMETER)
             .iterate();
        }

        // TODO this should merged into whichFunctionOwnsWhichParameters
        private static void fixParserParameters(GraphTraversalSource g) {
            g.V().has(Dom.Syn.V.CLASS, "ParserTypeDeclarationContext").as("func")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parameterList").inV()
                .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonEmptyParameterList").inV())
                .emit()
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parameter").inV()
                .addE(Dom.SITES).from("func")
                .property(Dom.Sites.ROLE, Dom.Sites.Role.HAS_PARAMETER)
                .iterate();
        }


        private static void whichArgumentsInstantiateWhichParameters(GraphTraversalSource g) {

           List<Edge> es = g.E().hasLabel(Dom.SITES).has(Dom.Sites.ROLE, Dom.Sites.Role.CALLS).toList();
           for (Edge edge : es) {
                Vertex func = edge.inVertex(); 
                Vertex call = edge.outVertex(); 

                List<Vertex> args = 
                    g.V(call).outE(Dom.SITES)
                    .has(Dom.Sites.ROLE, Dom.Sites.Role.HAS_ARGUMENT).inV()
                    .toList();

                List<Vertex> pars = 
                    g.V(func).outE(Dom.SITES)
                    .has(Dom.Sites.ROLE, Dom.Sites.Role.HAS_PARAMETER).inV()
                    .toList();

                if(args.size() != pars.size()){ 
                    System.err.println(g.V(call).elementMap().next());
                    System.err.println(g.V(func).elementMap().next());
                    throw 
                        new IllegalStateException("args.size() != pars.size()");
                }

                for (int i = 0; i < args.size(); i++) {
                    g.addE(Dom.SITES)
                     .from(args.get(i)).to(pars.get(i))
                     .property(Dom.Sites.ROLE, Dom.Sites.Role.INSTANTIATES)
                     
                     .iterate();
                }
           }
        }
}