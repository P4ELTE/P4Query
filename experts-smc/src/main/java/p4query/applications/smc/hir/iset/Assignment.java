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
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.exprs.StorageReference;
import p4query.applications.smc.hir.p4api.ProcedureDefinition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class Assignment implements Instruction {
   private StorageReference lhs;
   private Expression rhs;
   private IRType.SingletonFactory typeFactory;
   private Vertex v;

   // memcpy
   Assignment(CompilerState state, Vertex v, String vClass) {
         this.v = v;

         Vertex left;
         Vertex right;

         if(state.getG().V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "optInitializer").hasNext()){
            Map<String, Object> m = state.getG().V(v)
                  .project("left", "right")
                  .by(__.identity())
                  .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "optInitializer").inV()
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "initializer").inV()
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV())
                  .next();
            left = (Vertex) m.get("left");
            right = (Vertex) m.get("right");

         } else if(state.getG().V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "ASSIGN").hasNext()){
            Map<String, Object> m = state.getG().V(v)
                  .project("left", "right")
                  .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").inV())
                  .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV())
                  .next();
            left = (Vertex) m.get("left");
            right = (Vertex) m.get("right");
         } else {
            throw new IllegalStateException(
                      "Not an assignment or an initializer: " + state.getG().V(v).elementMap().next());
         }

         // note: left is expected to be a storage, it doesn't need a size hint
         Expression lhs0 = Expression.Factory.createLvalue(state, left);

         if(!(lhs0 instanceof StorageReference)){
            throw new IllegalStateException("Error: Assignment LHS is not a storage reference");
         }
         this.lhs = (StorageReference) lhs0;

         this.rhs = Expression.Factory.create(state, right, lhs.getTypeHint());

      //  // this will also add instructions
      //  StorageReference rhs2 = rhsExprToStorage(vClass, rhs, insts, ((P4StorageReference) lhs).getSizeOfPointed());
   //      if(!(rhs instanceof P4StorageReference || rhs instanceof CustomStorageReference)){
   //         throw new IllegalStateException("Error: Assignment RHS is not a storage reference nor a temp reference");
   //      }

   }

   @Override
   public String toString() {
      return "Assignment [lhs=" + lhs + ", rhs=" + rhs + "]";
   }

   @Override
   public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

      int lhsSize = lhs.getTypeHint().getSize();
      int rhsSize = rhs.getTypeHint().getSize();
      if(lhsSize != rhsSize){
         throw new IllegalStateException(
            String.format("LHS %s and RHS %s point to different sizes: %s vs %s", lhs, rhs, lhsSize, rhsSize));
      }

      List<StackInstruction> insts = new LinkedList<>();
      insts.add(new Comment(lhs.toP4Syntax() + " = " + rhs.toP4Syntax() ));

      // src
      insts.addAll(rhs.compileToLIR(local, global));

      // dst
      insts.addAll(lhs.compileToLIR(local, global));

      insts.add(new Const(new Size(lhsSize, lhs.toP4Syntax())));

      insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcpy", ""), new Size(3, "src, dst, length")));
      // every function has a return value, but memcpy's is not interesting now
      insts.add(new Pop());
      
      insts.add(new Comment("end of " + lhs.toP4Syntax() + " = " + rhs.toP4Syntax() ));
      return insts;
   }

   @Override
   public Vertex getOrigin() {
      return v;
   }




}
