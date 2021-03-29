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
package p4query.applications.smc.hir;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.iset.Instruction;
import p4query.applications.smc.hir.typing.IRType;
import p4query.ontology.Dom;

// NOTE this is polynomial time, but depth-first traversal is missing from gremlin
public class InstructionList {
   private List<Instruction> instructions;

   private GraphTraversalSource g;
   private Vertex sourceVertex;

   private IRType.SingletonFactory typeFactory;

   private Instruction.SingletonFactory factory;

   // TODO split this using a builder or something
   public InstructionList(GraphTraversalSource g, Vertex v, IRType.SingletonFactory typeFactory, ProcedureDefinition procDef, InstructionLayout.Builder layout){
      this.sourceVertex = v;
      this.g = g;
      this.typeFactory = typeFactory;

      Edge ee = g.V(v).outE(Dom.CFG)
                         .has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.ENTRY)
                         .next();

      Vertex entry = ee.inVertex();

      Set<Vertex> rets = g.V(v).outE(Dom.CFG)
                              .has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.RETURN)
                              .inV()
                              .toSet();

      // NOTE for the following, it would be more efficient to use this, but it
      //   does not work, because it's an error if the traversal in by() returns 
      //   empty: 
      //  ... 
      //  .project("normal", "true", "false")
      //  .by(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW))
      //  .by(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.TRUE_FLOW))
      //  .by(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW))
      //  .next();

      Map<Vertex, Vertex> flows = 
         g.E(ee).repeat(
                   __.inV().outE(Dom.CFG)
                     .or(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW),
                         __.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.TRUE_FLOW),
                         __.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW))) 
                .emit(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW))
                .toStream()
                .collect(
                    Collectors.toMap(e -> e.outVertex(), e -> e.inVertex()));
                  

      Map<Vertex, Vertex> trueFlows = 
         g.E(ee).repeat(
                   __.inV().outE(Dom.CFG)
                     .or(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW),
                         __.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.TRUE_FLOW),
                         __.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW))) 
                .emit(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.TRUE_FLOW))
                .toStream()
                .collect(
                   Collectors.toMap(e -> e.outVertex(), e -> e.inVertex()));
                

      Map<Vertex, Vertex> falseFlows = 
         g.E(ee).repeat(
                   __.inV().outE(Dom.CFG)
                     .or(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW),
                         __.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.TRUE_FLOW),
                         __.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW))) 
                .emit(__.has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FALSE_FLOW))
                .toStream()
                .collect(
                   Collectors.toMap(e -> e.outVertex(), e -> e.inVertex()));


      // TODO this could be useful elsewhere. add the ordering as graph edges.
      // this algorithm sorts vertices in pre-order (with true edges visited before false edges).
      //
      // put entry node in the stack. 
      // while stack is not empty:
      //   pop node s and add to instruction list.
      //   search for a flow edge starting from s. (there is only one.)
      //   if found, check if flow destination d was already in the instruction list.
      //   - if already there, register jump from s to d 
      //   - if not there, push d in the stack. 
      //   if there is no next node, check if this is a return node. 
      //   - if yes, continue.
      //   - if no: check if this there are conditional flows from the node.
      //      * if yes: 
      //        1. register cond jump from d to false node
      //        2. put false edge dest on the stack
      //        3. put true edge dest on the stack
      //      * if no: error

      LinkedHashSet<Vertex> verts = new LinkedHashSet<>();
      Stack<Vertex> stk = new Stack<>();
      Map<Vertex, Vertex> cjmps = new HashMap<>();
      Map<Vertex, Vertex> jmps = new HashMap<>();

      stk.push(entry); 
      while(!stk.isEmpty()){
         Vertex s = stk.pop();
         verts.add(s);

         if(flows.containsKey(s)){
            Vertex d = flows.get(s);
            if(verts.contains(d)){
               jmps.put(s,d);
            } else {
               stk.push(d);
            }
            continue;
         }

         if(rets.contains(s)){
            continue;
         }

         if(falseFlows.containsKey(s)){
            Vertex d = falseFlows.get(s);
            stk.push(d);
            cjmps.put(s, d);
         } else {
            System.err.println("Warning: no false edge");
         }

         if(trueFlows.containsKey(s)){
            Vertex d = trueFlows.get(s);
            stk.push(d);
         } else {
            System.out.println(trueFlows);
            System.err.println(g.V(s).elementMap().next());
            
            throw new IllegalStateException("Error: found non-return CFG node without out-flow");
         }
      }

      this.instructions = new LinkedList<>();

      layout.registerAllCondJumps(cjmps);
      layout.registerAllJumps(jmps);

      factory = new Instruction.SingletonFactory(g, cjmps, jmps, typeFactory, procDef);
      for (Vertex vert : verts) {
         this.instructions.addAll(factory.create(vert));
      }

// Instruction will use the CFG to add returns, so this is not needed.
//      if(procedureDefinition != null)
//         this.instructions.add(new ProcedureDone(procedureDefinition));
   }

   public List<Instruction> getList(){
      return instructions;

   }

   @Override
   public String toString() {
      return "InstructionList [instructions=" + instructions + "]";
   }

   public HashMap<Vertex, List<Instruction>> getIndex(){ 
      return factory.getIndex();
   }

}
