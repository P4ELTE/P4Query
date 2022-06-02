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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.ProcedureDeclaration;
import p4query.applications.smc.hir.p4api.ProcedureDefinition;
import p4query.applications.smc.hir.p4api.TableDeclaration;
import p4query.applications.smc.hir.p4api.externs.IsValid;
import p4query.applications.smc.hir.p4api.externs.SetInvalid;
import p4query.applications.smc.hir.p4api.externs.SetValid;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class ProcedureCallExpression implements Expression {

   // TODO this is redundant, callee already has these.
//   private IRType namespace = null;
//   private String name;
   private String origClass;
   private LinkedList<Expression> args = new LinkedList<>();
   private boolean hasZerothArg;
   private CompilerState state;

   private Declaration callee;

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

   ProcedureCallExpression(CompilerState state, Vertex src) throws UnableToParseException {
      this.state = state;
      GraphTraversalSource g = state.getG();

      if (!g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").hasNext()){
         throw new UnableToParseException(ProcedureCallExpression.class, src);
      }

      this.origClass = (String) g.V(src).values(Dom.Syn.V.CLASS).next();

      if (g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "ASSIGN").hasNext())
         throw new IllegalArgumentException(String.format(
               "Cannot initialize ProcedureCall from vertex %s (this is an assignment, not a method call).", src));

      this.callee = findCallee(src);

      if(callee instanceof TableDeclaration){
         handleTableApplication(src);
      } else {
         List<Vertex> zerothArgWrap = 
                  g.V(src)
                  .coalesce(
                        __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").inV().outE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue")
                              .inV(),
                        __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().outE(Dom.SYN)
                              .has(Dom.Syn.E.RULE, "expression").inV())
                  .toList();
         this.hasZerothArg = !zerothArgWrap.isEmpty();

         fillArguments(src);

         if (this.hasZerothArg) {
            Vertex zerothArg = zerothArgWrap.get(0);
            handleZerothArg(src, zerothArg);
         }
      }

      if(callee.getParameters().getFields().size() != args.size()){
         throw new IllegalStateException(
            String.format(
               "Call and callee has different arities.%nCall: %s.%nCallee: %s.%nCallee signature: %s.%nArguments:%s.", 
               args.size(), 
               callee.getParameters().getFields().size(), 
               callee,
               args.stream()
                   .map(e -> e.toP4Syntax())
                   .collect(Collectors.toList())));
      }
   }

   // note: isValid and apply has no call site (p4 language "bug")
   private Declaration findCallee(Vertex src) {

      List<Vertex> maybeCalleeV = 
            state.getG().V(src)
               .outE(Dom.SITES).has(Dom.Sites.ROLE, Dom.Sites.Role.CALLS)
               .inV()
               .toList();

      Vertex calleeV = null;
      if(!maybeCalleeV.isEmpty()){
         calleeV = maybeCalleeV.get(0);
      }
      boolean isTable = 
         calleeV != null &&
         state.getG().V(calleeV)
              .values(Dom.Syn.V.CLASS)
              .next()
              .equals("TableDeclarationContext");
      
      if(calleeV != null){

         Declaration tmpDef;
         CompilerState nState = new CompilerState(state);
         if(isTable){
            tmpDef = new TableDeclaration(nState, calleeV);
         } else {
            tmpDef = new ProcedureDeclaration(nState.getG(), calleeV, nState.getTypeFactory());
         }
         String name = tmpDef.getName();
         String ns = tmpDef.getNamespace();

         for (Declaration def : state.getDeclarations()) {
            if(ns == null && def.getNamespace() == null && def.getName().equals(name)){
               return def;
            } else if(ns != null && ns.equals(def.getNamespace()) && name.equals(def.getName())){
               return def;
            }
         }
         System.err.println(state.getDeclarations());
         throw new IllegalStateException(
            String.format("No signature found for called procedure %s::%s.", ns, name));
      } else {
         try {
            String calleeName = (String)
               state.getG()
                  .V(src)
                  .outE(Dom.SYN)
                  .or(__.has(Dom.Syn.E.RULE, "expression"),
                      __.has(Dom.Syn.E.RULE, "lvalue"))
                  .inV()
                  .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                  .repeat(__.outE(Dom.SYN).inV())
                  .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                  .values("value")
                  .next();

            if(calleeName.equals("isValid")){
               return new IsValid();
            } else if(calleeName.equals("setInvalid")){
               return new SetInvalid();
            } else if(calleeName.equals("setValid")){
               return new SetValid();
            } else {
               throw new IllegalStateException("Cannot handle callee named " + calleeName);
            }

         } catch(NoSuchElementException e){
            System.err.println(state.getG().V(src).elementMap().next());
            throw e;
         }
      }

   }

   private void handleTableApplication(Vertex src) {
      GraphTraversalSource g = state.getG();

      List<Vertex> maybeTable = 
         g.V() .has(Dom.Syn.V.CLASS, "TableDeclarationContext")
               .filter(
                     __.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                     .inV().has(Dom.Syn.V.VALUE, callee.getName()))
               .filter(
                     __.repeat(__.in(Dom.SYN))
                     .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))
                     .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                     .inV().has(Dom.Syn.V.VALUE, callee.getNamespace()))
               .toList();

      if(maybeTable.isEmpty()){
         throw new IllegalStateException("No table declarations found for callee " + callee);
      } else if(maybeTable.size() > 1){
         throw new IllegalStateException("Multiple table declarations found for callee " + callee);
      }

      Vertex table = maybeTable.get(0);

      LinkedHashMap<String, IRType> parentFields =  
         Declaration.stealParamsFromParentControl(state.getG(), table, state.getTypeFactory());

      for (Map.Entry<String, IRType> entry : parentFields.entrySet()) {
         args.add(new CustomStorageReference(state, "TableDeclarationContext", entry.getKey(), entry.getValue(), null));
      }
   }

   // Method dispatch is done Python-style: 
   // - packet.extract(hdr) is transformed to extract(packet, hdr). packet is the 0th argument of extract.
   private void handleZerothArg(Vertex src, Vertex zerothArg) {
      GraphTraversalSource g = state.getG();

      LinkedHashMap<String, IRType> pars = (LinkedHashMap<String, IRType>) callee.getParameters().getFields();
      if(pars.isEmpty()){
         throw new IllegalStateException(
            String.format("Called function %s::%s with zeroth arg, but its parameter list is empty. Maybe an extern definition is missing parameters?", callee.getNamespace(), callee.getName()));
      }

      IRType zerothParType = pars.values().iterator().next();

      args.addFirst(Expression.Factory.create(state, zerothArg, zerothParType));

   }

   private void fillArguments(Vertex src) {
      GraphTraversalSource g = state.getG();

      if (!g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").hasNext())
         throw new IllegalArgumentException(String.format("Cannot initialize ProcedureCall from %s vertex %s", src));

      fillNestedScopeArgs(src);
      // note: args at this point contains nested scope args, but not own arguments

      List<Vertex> argsVerts = g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.CLASS, "ArgumentContext")).outE(Dom.SYN)
            .has(Dom.Syn.E.RULE, "expression").inV().toList();

      Collections.reverse(argsVerts);
         
      LinkedHashMap<String, IRType> pars = (LinkedHashMap<String, IRType>) callee.getParameters().getFields();
      ArrayList<IRType> parTypes = new ArrayList<>(pars.values());

      int i = (hasZerothArg ? 1 : 0) + args.size() ; // will not count zeroth arg, and neither nested scope args. if none of these exist, then i start from 0.

      if(i >= pars.size() && !argsVerts.isEmpty()){
         System.err.println(g.V(argsVerts).elementMap().toList());
         
         throw new IllegalStateException(
            String.format("Start index is %s, but there are only %s parameters.%n Maybe an extern definition is missing parameters?%n Procedure %s::%s was called with %s arguments. Index formula is (%s ? 1 : 0) + %s.%n Parameters are %s.", 
               i, pars.size(), callee.getNamespace() , callee.getName(), argsVerts.size(), hasZerothArg, args.size(), pars )
         ); 
      }

      for (Vertex a : argsVerts) {

         IRType parType = parTypes.get(i);
         Expression expr = Expression.Factory.create(state, a, parType);
         if(expr == null){
            System.err.println("Warning: failed to create expression from argument " + g.V(a).elementMap().next() + ".");
         } else {
            args.add(expr);
         }

         i += 1;
      }
   }

   // Arguments "inherited" from the parent scope. E.g. when an action is defined inside a control, the arguments of control can also be used in the action.
   private void fillNestedScopeArgs(Vertex src) {

      GraphTraversalSource g = state.getG();

      Vertex decl;
      try {
         decl = g.V(src).outE(Dom.SYN)
                        .has(Dom.Syn.E.RULE, "lvalue").inV()
                        .repeat(__.outE(Dom.SYN).inV())
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
                        .next();

         if(g.V(decl).values(Dom.Syn.V.CLASS).next().equals("ActionDeclarationContext")){

            for (Map.Entry<String, IRType> field : state.getParentDecl().getParameters().getFields().entrySet()) {
               args.add(new CustomStorageReference(state, "generated reference", field.getKey(), field.getValue(), this));
            }
            
         }

      } catch(NoSuchElementException e){
         if (Arrays.asList("isValid", "setValid", "setInvalid").contains(this.callee.getName())) {
            // skip
         } else if(callee instanceof TableDeclaration){
            // skip
         } else if(g.V(src).has(Dom.Syn.V.CLASS, "DirectApplicationContext").hasNext()){ // direct application of control
            // skip
         } else {
//            System.err.println(this.name);
            System.err.println(callee);
            System.err.println(g.V(src).elementMap().next());
            throw new IllegalStateException("Callee has no declaration node", e);
         }
      }

   }

   @Override
   public String toString() {
      return "ProcedureCallExpression [callee=" + callee + ", origClass=" + origClass
            + ", args=" + args + "]";
   }

   @Override
   public String toP4Syntax() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.callee.getNamespace());
      sb.append("::");
      sb.append(this.callee.getName());
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

      String ns = this.callee.getNamespace();
      insts.add(new Invoke(new UnresolvedNameLabel(ns, this.callee.getName(), ""), new Size(args.size(), "")));

      // TODO there should be a better separation of procedures and functions
      if (!Arrays.asList("isValid", "setValid", "setInvalid").contains(this.callee.getName())) {
         insts.add(new Pop());
      } else {
         insts.add(new Comment("return value is used"));
      }
      insts.add(new Comment("end of " + this.toP4Syntax()));

      return insts;
   }

   public Declaration getCallee() {
     return callee;
   }


    @Override
    public IRType getTypeHint() {
        throw new IllegalStateException("ProcedureCallExpression has no size hint, yet someone called getSizeHint().");
    }
}
