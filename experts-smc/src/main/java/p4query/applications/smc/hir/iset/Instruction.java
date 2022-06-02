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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.ProcedureDefinition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

// TODO this class needs a redesign. everything should go into its respective class
public interface Instruction {

    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global);
    public Vertex getOrigin();

    public static class SingletonFactory {
        private HashMap<Vertex, List<Instruction>> vertInsts = new HashMap<>();

        private Map<Vertex, Vertex> cjmps; 
        private Map<Vertex, Vertex> jmps;
        private CompilerState state ;
        public SingletonFactory(CompilerState state, Map<Vertex, Vertex> cjmps, Map<Vertex, Vertex> jmps){
            this.cjmps = cjmps;
            this.jmps = jmps;
            this.state = state;
        }
        
        public List<Instruction> create(Vertex v){
            if(vertInsts.containsKey(v)){
                return vertInsts.get(v);
            } else {
                String vClass = (String) state.getG().V(v).values(Dom.Syn.V.CLASS).next();
                return create(v, vClass);
            }
        }

        public HashMap<Vertex, List<Instruction>> getIndex(){ 
            return vertInsts;
        }

        private List<Instruction> create(Vertex v, String vClass) {
            // note: uncomment this to see just the order the vertexes in the output
            // return Arrays.asList(new NoOp(vClass))

            LinkedList<Instruction> insts = new LinkedList<>();
            switch(vClass){
                case "ExpressionContext" : insts.add(new ProcedureCall(state, v,vClass)); break;
                case "AssignmentOrMethodCallStatementContext" : insts.add(createFromAOM(v,vClass)); break;
                case "DirectApplicationContext" : insts.add(new ProcedureCall(state, v,vClass)); break;
                case "ConditionalStatementContext" : insts.add(new ConditionalHead(state, v, vClass)); break;
                case "SelectExpressionContext" : insts.add(new SelectHead(state, v, vClass)); break;
                case "SelectCaseContext" : insts.add(new SelectCase(state,  v, vClass)); break;
                case "ParserStateContext" : insts.add(new NoOp(state, v, vClass)); break;
                case "BlockStatementContext" : insts.add(new NoOp(state, v, vClass)); break;
                case "VariableDeclarationContext" : handleDeclarations(insts, state, v, vClass); break;
                default: 
                    throw new IllegalArgumentException(
                        String.format("create does not know how to handle vertex %s of type %s.", v, vClass));
            }

            if(state.getG().V(v).inE(Dom.CFG).has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.RETURN).hasNext() 
               && !vClass.equals("ConditionalStatementContext")){
                // note: conditional statements are only returns when they are don't have a false edge, and neither a successor statement. this case is handled elsewhere
                insts.add(new ProcedureDone(state));
            }
            return insts;
        }


        private Instruction createFromAOM(Vertex v, String vClass) {
            if(state.getG().V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "ASSIGN").hasNext()){
                return new Assignment(state, v,vClass);
            } else {
                return new ProcedureCall(state, v, vClass);
            }
        }

        private void handleDeclarations(LinkedList<Instruction> insts, CompilerState state2, Vertex v, String vClass) {
            if(state.getG().V(v)
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "optInitializer").inV()
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "initializer").inV()
                    .hasNext()){
                insts.add(new Assignment(state, v,vClass));
            } else {
                return;
            }
        }

    }

}
