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
package p4query.experts.controlflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.function.Lambda;

import p4query.ontology.Dom;
import p4query.ontology.Status;

import org.codejargon.feather.Provides;

import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.ControlFlow;
import p4query.ontology.analyses.SymbolTable;
import p4query.ontology.analyses.SyntaxTree;

public class ControlFlowAnalysis {


    // NOTE: parsers are not structured language elements, we don't add return edges between parser states
    // NOTE: parser control flow is complicated, but it is because of the grammar
    // TODO: test on 1-way conditionals
        @Provides
        @Singleton
        @ControlFlow
        public Status analyse(GraphTraversalSource g, 
                            @SyntaxTree Status st, 
                            @AbstractSyntaxTree Status ast, 
                            @SymbolTable Status sym) {
            long startTime = System.currentTimeMillis();

        // // query printing
        //        File f = File. createTempFile("query", ".tex");
        //        PrintStream ps = new PrintStream(f);
        //        ControlFlowAnalysis.Control3.printQuery(ps);
        //        System.out.println("query printed to " + f.getAbsolutePath());
        //        ps.close();
        //        System.exit(0);

        System.out.println(ControlFlow.class.getSimpleName() + " started.");
        System.out.println("========");
        //why all, not just ord 0?
        // g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").outE().has(Dom.Syn.E.ORD, 0).inV().V()
            
            // firstTaskDynamic(g, 2);
            
            firstTaskDynamicAny(g, "hdr#ethernet#dstAddr");
            firstTaskDynamicAny(g, "hdr#ipv4#ttl");
            // keys =  g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").outE().has(Dom.Syn.E.ORD, 0).inV().has(Dom.Syn.V.CLASS, "NameContext")
            // .group()
            // .by(__.values(Dom.Syn.V.NODE_ID))
            // .by(__.values(Dom.Syn.V.CLASS)).next();
            // System.out.println(keys);
            
            // firstTaskManual(g);
            
            
            addFlowToFirstStatement(g);
            addFlowToTwoWayConditionals(g);
            addFalseFlowToOneWayConditionals(g);
            addFlowBetweenSiblings(g);
            addFlowBetweenParserStates(g);
            addParserEntry(g);
            addParserExit(g);
            addEntryExit(g);

            quickfixSelectInCFG(g);

            System.exit(1);
            long stopTime = System.currentTimeMillis();
            System.out.println(String.format("%s complete. Time used: %s ms.", ControlFlow.class.getSimpleName() , stopTime - startTime));
            return new Status();
        }

        private static void firstTaskDynamicAny(GraphTraversalSource g, String toSearch){
            int[] ords = {0,2};
            String[] values = toSearch.split("#");

            for( int ord : ords){
                ArrayList<Object> lines = new ArrayList<Object>();
                for(int i = 0; i<values.length; ++i){
                    System.out.print(values[i] + (i+1 < values.length?".":"\n"));
                    Map<Object,Object> keys =  g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").outE().has(Dom.Syn.E.ORD, ord).inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.VALUE, values[i]))
                    
                    .group()
                    .by(__.values(Dom.Syn.V.NODE_ID))
                    .by(__.values(Dom.Syn.V.LINE)).next();
                    // System.out.print("keys " + values[i] +": ");
                    // System.out.println(keys);
                    if (i == 0){
                        for (Map.Entry<Object,Object> key : keys.entrySet()){
                            lines.add(key.getValue());
                        }
                    }else{
                        ArrayList<Object> tmp = new ArrayList<Object>();
                        for (Map.Entry<Object,Object> key : keys.entrySet()){
                            for(Object line : lines){
                                if(line.equals(key.getValue())){
                                    tmp.add(line);
                                }
                            }
                        }
                        // System.out.print("lines before ");
                        // System.out.println(lines);
                        lines = tmp;
                        // System.out.print("lines after ");
                        // System.out.println(lines);
                    }
                }
                
                System.out.print("On the " + (ord == 0? "left":"right") + ": \n");

                for(Object line : lines){
                    System.out.println("line: " + line);
                }
            }
            System.out.println("----------------");
        }

        private static void firstTaskDynamic(GraphTraversalSource g, int ord){
            Map<Object,Object> keys_hdr =  g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").outE().has(Dom.Syn.E.ORD, ord).inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.VALUE, "hdr"))
            
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.values(Dom.Syn.V.LINE)).next();
            //System.out.println(keys_hdr);

            Map<Object,Object> keys_ethernet =  g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").outE().has(Dom.Syn.E.ORD, ord).inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.VALUE, "ethernet"))
            
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.values(Dom.Syn.V.LINE)).next();
            //System.out.println(keys_ethernet);

            Map<Object,Object> keys_dstAddr =  g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").outE().has(Dom.Syn.E.ORD, ord).inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.VALUE, "dstAddr"))
            
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.values(Dom.Syn.V.LINE)).next();
            //System.out.println(keys_dstAddr);

            ArrayList<Object> line_on_left = new ArrayList<Object>();
            for (Map.Entry<Object,Object> hdr : keys_hdr.entrySet()){
                for (Map.Entry<Object,Object> ethernet : keys_ethernet.entrySet()){
                    if(hdr.getValue().equals(ethernet.getValue())){
                        for (Map.Entry<Object,Object> dstAddr : keys_dstAddr.entrySet()){
                            if(hdr.getValue().equals(dstAddr.getValue())){
                                line_on_left.add(dstAddr.getValue());
                            }
                        }
                    }                    
                }
            }
            System.out.print("On the " + (ord == 0? "left":"right") + ": line: ");

            for(Object line : line_on_left){
                System.out.println(line);
            }
        }

        private static void firstTaskManual(GraphTraversalSource g){
            System.out.print("On the right: ");
            //repeat until - feltetel teljesul
            /*
            outE inV a syn eleken
            keys =  g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext")
            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            */
            Boolean found = true;
            Map<Object, Object> keys =
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().has(Dom.Syn.V.CLASS, "ExpressionContext").outE().inV().has(Dom.Syn.V.CLASS, "ExpressionContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "ExpressionContext").outE().inV().has(Dom.Syn.V.CLASS, "NonTypeNameContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "Type_or_idContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "TerminalNodeImpl").has(Dom.Syn.V.VALUE, "hdr")
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.values(Dom.Syn.V.LINE)).next();
            found = found && keys.size() > 0;

            keys = 
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().has(Dom.Syn.V.CLASS, "ExpressionContext").outE().inV().has(Dom.Syn.V.CLASS, "ExpressionContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "NameContext").outE().inV().has(Dom.Syn.V.CLASS, "NonTypeNameContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "Type_or_idContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "TerminalNodeImpl").has(Dom.Syn.V.VALUE, "ethernet")
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.values(Dom.Syn.V.LINE)).next();

            //System.out.println("---------------------- Keys --------------------\n" + keys);
            


            //System.out.println("---------------------- Keys --------------------\n" + keys);
            found = found && keys.size() > 0;
            keys = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().has(Dom.Syn.V.CLASS, "ExpressionContext").outE().inV().has(Dom.Syn.V.CLASS, "NameContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "NonTypeNameContext").outE().inV().has(Dom.Syn.V.CLASS, "Type_or_idContext")
            .outE().inV().has(Dom.Syn.V.CLASS, "TerminalNodeImpl").has(Dom.Syn.V.VALUE, "dstAddr")
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.values(Dom.Syn.V.LINE)).next();

            //System.out.println("---------------------- Keys --------------------\n" + keys);
            found = found && keys.size() > 0;

            
            if(found){
                for (Map.Entry<Object,Object> entry : keys.entrySet())
                    System.out.println("line: " + entry.getValue());
            }
        }

        // send flow from each block to its first statement (possibly another block)
        private static void addFlowToFirstStatement(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN)
             .or(__.has(Dom.Syn.V.CLASS, "BlockStatementContext"),
                 __.has(Dom.Syn.V.CLASS, "ParserBlockStatementContext"),
                 __.has(Dom.Syn.V.CLASS, "ParserStateContext"))
             .as("b")
             .map(
                __.outE(Dom.SEM).or(
                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.STATEMENT),
                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST))
                .order().by(Dom.Cfg.E.ORD, Order.asc)
                .limit(1))
             .inV()
             .addE(Dom.CFG).from("b")
             .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
             .iterate();
        }

            // send flow from each conditional to both of its branches 
        private static void addFlowToTwoWayConditionals(GraphTraversalSource g) {
            List<Map<String, Object>> branches = 
                g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ConditionalStatementContext").as("c")
                .outE(Dom.SEM).or(
                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH),
                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH))
                .as("e")
                .select("c", "e")
                .toList();

            for (Map<String, Object> m : branches){
                Vertex c = (Vertex) m.get("c");
                Edge e = (Edge) m.get("e");
                String role = (String) g.E(e).values(Dom.Sem.ROLE).next();

                String label;
                if(role.equals(Dom.Sem.Role.Control.TRUE_BRANCH)){
                    label = Dom.Cfg.E.Role.TRUE_FLOW;
               } else if(role.equals(Dom.Sem.Role.Control.FALSE_BRANCH)){
                    label = Dom.Cfg.E.Role.FALSE_FLOW;
               } else {
                   throw new IllegalStateException("Unexpected role " + role);
               }
               g.E(e).inV()
                .addE(Dom.CFG).from(c)
                .property(Dom.Cfg.E.ROLE, label)
                .iterate();
            }
        }

        // note: conditionals always have a true branch
        // descr: The false flow should point to the next sibling of the first ancestor that is
        // - not the child of a conditional node, and
        // - not the last child of a block node.
        // If no such ancestors exists (i.e. if the conditional itself is a return point in the function), then no false flow is created, but a return edge is pointed to the conditional (see addEntryExit())
        private static void addFalseFlowToOneWayConditionals(GraphTraversalSource g) {
            LinkedHashMap<Vertex, Vertex> condsAncestors = findOneWayCondsAncestsors(g);

            for (Map.Entry<Vertex, Vertex> entry : condsAncestors.entrySet()) {
                Vertex c = entry.getKey();
                Vertex ancestor = entry.getValue();

                if(ancestor == null){
                    continue;
                } 
                Long ownEdgeId; 
                try {
                    ownEdgeId = (Long)
                        g.V(ancestor)
                            .inE(Dom.SEM)
                            .has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST) // note NEST is enough, blockstatementcontext is never linked by STATEMENT edges
                            .values(Dom.Sem.ORD)
                            .next();
                } catch(NoSuchElementException e){
                    System.err.println(g.V(ancestor).elementMap().next());
                    throw e;
                }

                List<Map<String, Object>> sibs = 
                    g.V(ancestor)
                        .inE(Dom.SEM)
                        .has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST) 
                        .outV()
                        .outE(Dom.SEM)
                        .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST),
                            __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.STATEMENT))
                        .order().by(Dom.Sem.ORD, Order.asc)
                        .project("edgeOrd", "sibling")
                        .by(__.values(Dom.Sem.ORD))
                        .by(__.inV())
                        .toList();

                // assumes sibs is sorted by edge ord
                Vertex nextSib = null;
                for (Map<String, Object> obj : sibs) {
                    Long sibId = (Long) obj.get("edgeOrd");
                    Vertex sib = (Vertex) obj.get("sibling");
                    if(sibId > ownEdgeId){
                        nextSib = sib;
                        break;
                    }
                }
                if(nextSib == null){
                    throw new IllegalStateException("Next sibling not found.");
                }

                g.V(c).addE(Dom.CFG).to(nextSib)
                      .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW)
                      .iterate();

            }
        }

        private static LinkedHashMap<Vertex,Vertex> findOneWayCondsAncestsors(GraphTraversalSource g){

            LinkedHashMap<Vertex,Vertex> condsAncenstors = new LinkedHashMap<>();

            List<Vertex> oneWayConds = 
                g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ConditionalStatementContext")
                 .not(__.outE(Dom.SEM)
                        .has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH))
                 .toList();

            for (Vertex c : oneWayConds) {
                List<Vertex> maybeAncestor = 
                    g.V(c)
                    .until(__.not(__.inE(Dom.SEM)
                                    .or(
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.BODY),
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.LAST),
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH),
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH))))
                    .repeat(__.inE(Dom.SEM)
                            .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.STATEMENT),
                                __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST),
                                __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH),
                                __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH))
                            .outV())
                    .toList();

                if(maybeAncestor.isEmpty()){
                    condsAncenstors.put(c, null);
                } else {
                    condsAncenstors.put(c, maybeAncestor.get(0));
                } 
            }
            return condsAncenstors;
        }

        // for each child c, send flow from c's return point to c's sibling
        private static void addFlowBetweenSiblings(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN).or(
                __.has(Dom.Syn.V.CLASS, "BlockStatementContext"),
                __.has(Dom.Syn.V.CLASS, "ParserBlockStatementContext"))
             .local(
                __
                  .sideEffect(Lambda.consumer("{ t -> t.sideEffects(\"r\").clear() }"))
                  .outE(Dom.SEM)
                  .or(
                      __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.STATEMENT),
                      __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST))
                  .order().by(Dom.Cfg.E.ORD, Order.asc)
                  .inV()

                    // NOTE: this is in sideEffect for the depth-first effect:
                    //   each child is fully processed before we start  
                    //   processing the next one
                  .sideEffect(
                      __.as("b")
                      // NOTE: this is in sideEffect so b is aggregated even  
                      //   if r is empty. we also don't need the result.
                      .sideEffect(
                        __.flatMap(__.cap("r").unfold()) // cuts traversal if r empty
//                          .sideEffect(Lambda.consumer("{ t -> System.out.println t.get().value(\"nodeId\") }"))
                          .optional(__.outE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.RETURN).inV())
                          .addE(Dom.CFG).to("b")
                          .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW))
                      .sideEffect(Lambda.consumer("{ t -> t.sideEffects(\"r\").clear() }"))
                      .aggregate("r")))
             .iterate();
        }

        // TODO "inline" into this the query from aliases (eliminate NEXT)
        @SuppressWarnings("unchecked")
        private static void addFlowBetweenParserStates(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN)
             .has(Dom.Syn.V.CLASS, "ParserStateContext").as("psc")
//           .sideEffect(Lambda.consumer("{ t -> System.out.println \"S\" }"))

            // this depends on addFlowToFirstStatement, but its easy to eliminate
             .optional(__.outE(Dom.CFG).has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW).inV()
                         .optional(__.outE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.RETURN).inV()))
             .as("body")
             .select("psc")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "transitionStatement").inV()
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "stateExpression").inV()

//             .sideEffect(Lambda.consumer("{ t -> System.out.println \"A\" }"))
             .coalesce(

                // if stateExpression has a name, just resolve it to the next state
                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                  .repeat(__.out(Dom.SYN))
                  .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
//                  .sideEffect(Lambda.consumer("{ t -> System.out.println \"B1\" }"))
                  .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
//                  .sideEffect(Lambda.consumer("{ t -> System.out.println \"B2\" }"))
                  .addE(Dom.CFG).from("body")
                  .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
                  .inV(),

                // if stateExpression has a select expression, 
                //    resolve all names in the branches to their next state 
                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "selectExpression").inV()
                  .as("sel")
 //                 .sideEffect(Lambda.consumer("{ t -> System.out.println \"C1\" }"))
                  .addE(Dom.CFG).from("body")
                  .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)

                  // find the cases and add a flow
                  .<Vertex>select("sel")
                  .repeat(__.out(Dom.SYN))
                  .until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext")) 
                  .as("case")
                  .addE(Dom.CFG).from("sel")
                  .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)

                  // resolve the name in the case to its next state
                  .select("case")
                  .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                  .repeat(__.out(Dom.SYN))
                  .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                  .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
//                  .sideEffect(Lambda.consumer("{ t -> System.out.println \"C2\" }"))
                  .addE(Dom.CFG).from("case")
                  .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
                  .inV())
             .iterate();
            
        }

        // TODO "inline" into this the query from aliases (eliminate START)
        private static void addParserEntry(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN)
             .has(Dom.Syn.V.CLASS, "ParserDeclarationContext")
             .as("pdc")
             .outE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.START).inV()
             .addE(Dom.CFG).from("pdc")
             .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.ENTRY)
             .iterate();
        }

        @SuppressWarnings("unchecked")
        private static void addParserExit(GraphTraversalSource g) {
            // find all parser state references that are either "accept" or "reject"

            // this query handles simple transitions
            g.V().hasLabel(Dom.SYN)

             // find final states
             .has(Dom.Syn.V.CLASS, "StateExpressionContext").as("sec")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
             .repeat(__.out(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .filter(__.values("value").is(P.within("accept", "reject")))

             .<Vertex>select("sec")
             .repeat(__.in(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "ParserStateContext"))

              // select the return point and add the edge
              // this depends on addFlowToFirstStatement, but its easy to eliminate
             .optional(__.outE(Dom.CFG).has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW).inV()
                         .optional(__.outE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.RETURN).inV()))
             .as("ret")
             .<Vertex>select("sec")
             .repeat(__.in(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))
             .addE(Dom.CFG).to("ret")
             .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.RETURN)
             .iterate();

            // this query handles selects
            g.V().hasLabel(Dom.SYN)
             .has(Dom.Syn.V.CLASS, "StateExpressionContext")

             .repeat(__.out(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext"))
             .as("scc")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()

             .repeat(__.out(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .filter(__.values("value").is(P.within("accept", "reject")))

             // go up until you find the top-level declaration
             .repeat(__.in(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))
             .addE(Dom.CFG).to("scc")
             .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.RETURN)
             .iterate();

        }

        // add entry and exit edges to declaration
        private static void addEntryExit(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN).or(
                    __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                    __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"))
                .as("cdc")
                .outE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.BODY).inV()
                .addE(Dom.CFG).from("cdc")
                .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.ENTRY)
                .select("cdc")
                .outE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.RETURN).inV()
                .addE(Dom.CFG).from("cdc")
                .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.RETURN)
                .iterate();


            LinkedHashMap<Vertex,Vertex> condsAncenstors = findOneWayCondsAncestsors(g);

            for (Map.Entry<Vertex, Vertex> entry : condsAncenstors.entrySet()) {
                if(entry.getValue() != null)
                    continue;
                
                g.V(entry.getKey()).as("c")
                 .repeat(__.in(Dom.SYN))
                 .until(__.or(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                            __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext")))
                 .addE(Dom.CFG).to("c")
                 .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.RETURN)
                 .iterate();
            }
        }


    // TODO the specs (Section 11.6.) require triggering a runtime error if no cases match. the CFG requires exception-edges to handle this. (but no problem if the last pattern is 'default' or '_')
    // TODO this should be merged into the main algorithm
    // NOTE this introduces flows that go through each select case in order. (the original was sending special flows from the select expression top to select cases.) 
    private void quickfixSelectInCFG(GraphTraversalSource g) {

        // sets up flows to visit cases one by one. assumes that the select head pushes the head expression to the stack
        List<Map<String, Object>> sels = 
            g.V().has(Dom.Syn.V.CLASS, "SelectExpressionContext").as("sel")
                 .map( 
                    __.outE(Dom.CFG).has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
                      .order().by(Dom.Syn.E.ORD, Order.asc)
                      .inV()
                      .fold()).as("cases")
                 .select("sel", "cases")
                 .toList();

        for (Map<String,Object> map : sels) {
            Vertex sel = (Vertex) map.get("sel");

            Collection<Vertex> cases = (Collection<Vertex>) map.get("cases");

            // 1. delete flow edges from SelectExpression to SelectCases
            g.V(cases).inE(Dom.CFG)
             .has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
             .drop()
             .iterate();

            // 2. convert the flow from the SelectCase into a true-flow
            g.V(cases).outE(Dom.CFG)
                .has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
                .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.TRUE_FLOW)
                .iterate();

            // 3. set up false-flows between the SelectCases
            Iterator<Vertex> it = cases.iterator();
            if(!it.hasNext()) 
                throw new IllegalStateException("select expression has no cases: " + sel);

            Vertex caze = it.next();
            while(it.hasNext()){
                Vertex prevCaze = it.next();
                g.addE(Dom.CFG).from(prevCaze).to(caze)
                  .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW)
                  .iterate();
                caze = prevCaze;
            }

            // 4. add flow edge from SelectExpression to first SelectCase
            g.addE(Dom.CFG).from(sel).to(caze)
                .property(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW)
                .iterate();
        }
    }


}