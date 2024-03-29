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
package p4query.experts.callgraph;

import javax.inject.Singleton;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.codejargon.feather.Provides;

import p4query.ontology.Dom;
import p4query.ontology.Status;
import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.CallGraph;
import p4query.ontology.analyses.SymbolTable;
import p4query.ontology.analyses.SyntaxTree;

/**
 * Hello world!
 *
 */
public class CallGraphGenerator {

    @Provides
    @Singleton
    @CallGraph
    public Status analyse(GraphTraversalSource g, @SyntaxTree Status s, @AbstractSyntaxTree Status a, @SymbolTable Status t){

        long startTime = System.currentTimeMillis();
        System.out.println(CallGraph.class.getSimpleName() +" started.");

        whoCallsAction(g);
        whoCallsTable(g);
        whoCallsFunctionPrototype(g);
        whoCallsParserState(g);
        whoInvokesParsersAndControls(g);

        long stopTime = System.currentTimeMillis();
        System.out.println(String.format("%s complete. Time used: %s ms.", CallGraph.class.getSimpleName() , stopTime - startTime));

        return new Status();
    }
    public static void whoCallsTable(GraphTraversalSource g){
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "TableDeclarationContext").as("decl")
                .flatMap(
                __.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).inV()
                .repeat(__.in(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))
                .dedup())
                .addE(Dom.CALL).to("decl")
                .property(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                
                .iterate();
    }

    public static void whoCallsAction(GraphTraversalSource g){
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ActionDeclarationContext").as("decl")
            .flatMap(
            __.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).inV()
            .repeat(__.in(Dom.SYN))
            .until(__.or(__.has(Dom.Syn.V.CLASS, "TableDeclarationContext"),
                         __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext")))
            .dedup())
        .addE(Dom.CALL).to("decl")
        .property(Dom.Call.ROLE, Dom.Call.Role.CALLS)
        
        .iterate();
    }

    public static void whoCallsFunctionPrototype(GraphTraversalSource g){
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "FunctionPrototypeContext").as("decl")
            .flatMap(
            __.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).inV()
            .repeat(__.in(Dom.SYN))
            .until(
                __.or(
                    __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                    __.has(Dom.Syn.V.CLASS, "ParserStateContext"),
                    __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext")))
            .dedup())
        .addE(Dom.CALL).to("decl")
        .property(Dom.Call.ROLE, Dom.Call.Role.CALLS)
        
        .iterate();
    }

    public static void whoCallsParserState(GraphTraversalSource g){
        g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ParserStateContext").as("st")
            .flatMap(
            __.repeat(__.in(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))
            .dedup())
            .addE(Dom.CALL).to("st")
            .property(Dom.Call.ROLE, Dom.Call.Role.CALLS)
            
            .iterate();
    }

    // TODO this is technically wrong. first, they are "instantiated" by the top-level, and are passed to InstantiationContext which in turn is also an instantiation by the top-level. The functions are invoked by PackageTypeDeclaration which is an extern. 
    public static void whoInvokesParsersAndControls(GraphTraversalSource g){
        g.V().hasLabel(Dom.SYN)
                .or(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                    __.has(Dom.Syn.V.CLASS, "ParserDeclarationContext")).as("invokee")
                .flatMap(
                __.inE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Instantiation.INVOKES).outV()
                .repeat(__.in(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "InstantiationContext"))
                .dedup())
                .addE(Dom.CALL).to("invokee")
                .property(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                
                .iterate();

    }
}
