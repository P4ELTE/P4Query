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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.applications.smc.lir.typing.UnresolvedVertexLabel;
import p4query.ontology.Dom;

public class ProcedureCallExpression implements Expression {

   private IRType namespace = null;
   private String name;
   private GraphTraversalSource g;
   private IRType.SingletonFactory typeFactory;
   private String origClass;
   private Definition containerDef;
   private LinkedList<Expression> args = new LinkedList<>();

// NOTE there are no object-level methods in P4, only class-level methods.
// in packet.extract, packet is the 0th argument, and packet_in is the namespace
// of extract.

// NOTE syntax tree variations (is something wrong with the specs?):
//
// hdr.ipv4.isValid: 
// (expression
//   (expression
//     (expression ...) <-- 0th arg
//     (name ...)) <-- procedure name
//   (argumentList ...))
//
// packet.extract: 
// (assignmentOrMethodCall
// (lvalue
//   (lvalue ...) <-- 0th arg
//   (name ...) <-- procedure name
//   (argumentList ...))
//
// mark_to_drop: 
// (assignmentOrMethodcall
//   (lvalue
//      (prefixedNonTypeName ...)) <-- procedure name
//   (argumentList ...))
//
// MyIngress: 
// (expression
//   (expression
//     (nonTypeName ...)) <-- procedure name
//   (argumentList ...))
//
// ipv4_lpm.apply() : 
//   (directApplication
//     (typeName ...)
//     (APPLY)
//     (argumentList ...))

   ProcedureCallExpression(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory, Definition containerDef) {
      this.g = g;
      this.typeFactory = typeFactory;
      this.origClass = (String) g.V(src).values(Dom.Syn.V.CLASS).next();
      this.containerDef = containerDef;

      if (g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "ASSIGN").hasNext())
         throw new IllegalArgumentException(String.format(
               "Cannot initialize ProcedureCall from vertex %s (this is an assignment, not a method call).", src));

      // there is always an argumentList edge (even if there are no arguments)
      fillArguments(src);

      if (origClass.equals("DirectApplicationContext")) {
         handleDirectApplication(src);
         return;
      }

      List<Vertex> zerothArgWrap = g.V(src)
            .coalesce(
                  __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").inV().outE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue")
                        .inV(),
                  __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().outE(Dom.SYN)
                        .has(Dom.Syn.E.RULE, "expression").inV())
            .toList();

      if (!zerothArgWrap.isEmpty()) {
         Vertex zerothArg = zerothArgWrap.get(0);
         handleZerothArg(src, zerothArg);
      } else {

         this.name = (String) g.V(src).outE(Dom.SYN)
               .or(__.has(Dom.Syn.E.RULE, "lvalue"), __.has(Dom.Syn.E.RULE, "expression")).inV()
               .repeat(__.outE(Dom.SYN).inV()).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).values("value")
               .next();

      }
   }

   private void handleDirectApplication(Vertex src) {
      this.name = (String) 
         g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeName")
                 .inV()
                 .repeat(__.outE(Dom.SYN).inV())
                 .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                 .values("value").next();

      Vertex tableDecl = 
         g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeName")
                 .inV()
                 .repeat(__.outE(Dom.SYN).inV())
                 .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                 .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
                 .next();

      LinkedHashMap<String, IRType> parentFields =  
         Definition.stealParamsFromParentControl(g, tableDecl, typeFactory);

      for (Map.Entry<String, IRType> entry : parentFields.entrySet()) {
         args.add(new CustomStorageReference("TableDeclarationContext", entry.getKey(), entry.getValue(), null));
      }

   }

   private void handleZerothArg(Vertex src, Vertex zerothArg) {

      // TODO sizeHint should be found out from the declaration of the called function. othewise literals will have ambigious size
      args.addFirst(Expression.Factory.create(g, zerothArg, typeFactory, containerDef, -1));

      this.name = (String) g.V(src).outE(Dom.SYN)
            .or(__.has(Dom.Syn.E.RULE, "lvalue"), __.has(Dom.Syn.E.RULE, "expression")).inV().outE(Dom.SYN)
            .has(Dom.Syn.E.RULE, "name").inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).values("value").next();

      Vertex typeVert = g.V(zerothArg)
                         .repeat(__.outE(Dom.SYN).inV())
                         .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
            // NOTE: optional will run in case of structs. it will not run in case of base
            // types.
            .optional(__.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV())
            .next();

      // TODO in theory, this will lead to errors, if isValid is a valid identifier
      // for custom extern methods.
      // in this case the namespace should be the extern, not the global (null)
      if (Arrays.asList("isValid", "setValid", "setInvalid").contains(this.name)) {
         this.namespace = null;
      } else {
         this.namespace = typeFactory.create(typeVert);
      }
   }

   private void fillArguments(Vertex src) {
      if (!g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").hasNext())
         throw new IllegalArgumentException(String.format("Cannot initialize ProcedureCall from %s vertex %s", src));

      List<Vertex> argsVerts = g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.CLASS, "ArgumentContext")).outE(Dom.SYN)
            .has(Dom.Syn.E.RULE, "expression").inV().toList();

      for (Vertex a : argsVerts) {

         // TODO sizeHint should be found out from the declaration of the called function. othewise literals will have ambigious size
         Expression expr = Expression.Factory.create(g, a, typeFactory, containerDef, -1);
         if(expr == null){
            System.err.println("Warning: failed to create expression from argument. (Was it an expression list?)");
         } else {
            args.add(expr);
         }
      }
   }

   @Override
   public String toString() {
      return "ProcedureCallExpression [name=" + name + ", namespace=" + namespace + ", origClass=" + origClass
            + ", args=" + args + "]";
   }

   @Override
   public String toP4Syntax() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.name);
      sb.append("(");
      String delim = "";
      for (Expression exp : args) {
         sb.append(delim);
         sb.append(exp.toP4Syntax());
         delim = ", ";
      }
      sb.append(")");
      return sb.toString();
   }

   @Override
   public StorageReference getStorageReference() {
      throw new IllegalStateException("ProcedureCallExpression has no local storage, yet someone called getStorageReference().");
   }

   @Override
   public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

      List<StackInstruction> insts = new LinkedList<>();
      insts.add(new Comment(this.toP4Syntax()));

      for (Expression arg : args) {
         insts.addAll(arg.compileToLIR(local, global));
      }
      String ns = this.namespace == null ? "core" : this.namespace.getName();
      insts.add(new Invoke(new UnresolvedNameLabel(ns, this.name, ""), new Size(args.size(), "")));

      // TODO there should be a better separation of procedures and functions
      if (!Arrays.asList("isValid", "setValid", "setInvalid").contains(this.name)) {
         insts.add(new Pop());
      } else {
         insts.add(new Comment("return value is used"));
      }

      return insts;
   }

   @Override
   public int getSizeHint() {
      throw new IllegalStateException("ProcedureCallExpression has no size hint, yet someone called getSizeHint().");
   }
}
