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
package p4query.applications.smc.hir;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.Header;
import p4query.applications.smc.Packet;
import p4query.applications.smc.hir.externs.IUseCase;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.externs.implem.V1ModelMain;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.ProcedureDeclaration;
import p4query.applications.smc.hir.p4api.ProcedureDefinition;
import p4query.applications.smc.hir.p4api.TableDeclaration;
import p4query.applications.smc.hir.p4api.TableDefinition;
import p4query.applications.smc.hir.p4api.externs.CounterCount;
import p4query.applications.smc.hir.p4api.externs.FillTables;
import p4query.applications.smc.hir.p4api.externs.IsValid;
import p4query.applications.smc.hir.p4api.externs.Main;
import p4query.applications.smc.hir.p4api.externs.MarkToDrop;
import p4query.applications.smc.hir.p4api.externs.MemCmp;
import p4query.applications.smc.hir.p4api.externs.MemCpy;
import p4query.applications.smc.hir.p4api.externs.PacketInExtract;
import p4query.applications.smc.hir.p4api.externs.PacketOutEmit;
import p4query.applications.smc.hir.p4api.externs.ReceivePacket;
import p4query.applications.smc.hir.p4api.externs.SetInvalid;
import p4query.applications.smc.hir.p4api.externs.SetValid;
import p4query.applications.smc.hir.p4api.externs.Subtract;
import p4query.applications.smc.hir.p4api.externs.UpdateChecksum;
import p4query.applications.smc.hir.p4api.externs.VerifyChecksum;
import p4query.applications.smc.hir.typing.GlobalStruct;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.Struct;
import p4query.applications.smc.lir.StackProgram;
import p4query.applications.smc.lir.iset.Alloc;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Exit;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class Program {

   private LinkedList<IRType> globals;
   private LinkedList<Declaration> declarations;
   private LinkedHashMap<Declaration, Definition> definitions;
   private GraphTraversalSource g;
   private IRType.SingletonFactory typeFactory;
   private InstructionLayout.Builder instLayout ;

   CompilerState state;
   private IUseCase useCase;
   private boolean nondet;

   public Program(GraphTraversalSource g, IUseCase useCase, boolean nondet){
      this.g = g;
      this.typeFactory = new IRType.SingletonFactory(g);
      this.globals = new LinkedList<>();
      this.declarations = new LinkedList<>();
      this.definitions = new LinkedHashMap<>();
      this.instLayout = new InstructionLayout.Builder();

      this.state = new CompilerState(globals, declarations, g, typeFactory, instLayout);

      this.useCase = useCase;
      this.nondet = nondet;

      fillStructTypes();

      fillDeclarations();

      fillDefinitions();
   }


   private void fillDeclarations() {
      List<Vertex> cfgSources = 
         g.V()
          .or(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
             __.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"),
             __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"),
             __.has(Dom.Syn.V.CLASS, "TableDeclarationContext"))
         .toList();

      for (Vertex src : cfgSources) {
         if(g.V(src).has(Dom.Syn.V.CLASS, "TableDeclarationContext").hasNext()){
            declarations.add(new TableDeclaration(state, src));
         } else {
            declarations.add(new ProcedureDeclaration(g, src, typeFactory));
         }
      }

      declarations.addAll(
         Arrays.asList(
            // stdlib
            new ReceivePacket(),
            new FillTables(),
            new MemCmp(),
            new MemCpy(),
            new Subtract(),
            // package
            new Main(),
            new IsValid(),
            new PacketInExtract(),
            new PacketOutEmit(),
            new UpdateChecksum(),
            new VerifyChecksum(),
            new MarkToDrop(),
            new CounterCount(),
            new SetInvalid(),
            new SetValid()
            ));
   }

   private void fillDefinitions() {
      for (Declaration dec : declarations) {
        if(dec instanceof ProcedureDeclaration) {
            ProcedureDeclaration pdec = (ProcedureDeclaration) dec;
            ProcedureDefinition pdef = new ProcedureDefinition(pdec);
            pdef.setDeclaration(pdec);
            pdef.init(state);
            definitions.put(pdec, pdef);
        }
      }
   }

   private void fillStructTypes() {
      globals.add(new GlobalStruct(g, typeFactory));

      List<Vertex> vs = 
         g.V().or(__.has(Dom.Syn.V.CLASS, "StructTypeDeclarationContext"),
                  __.has(Dom.Syn.V.CLASS, "HeaderTypeDeclarationContext"))
              .toList();
      for (Vertex v : vs) {
         Struct type = (Struct) typeFactory.create(v);
         if(type.isP4Struct()) 
            globals.add(type);
      }

   }

   public StackProgram compileToLIR() {

      for (Declaration decl : declarations) {
         globals.add(decl.getParameters().restrictToOwnedVars());
         globals.add(decl.getTemps());
         globals.add(decl.getControlLocals());
         globals.add(decl.getLocals());

      }

      useCase.addMemory(state);

      definitions.putAll(useCase.linkDefinitions(declarations, state));

      checkLinkage();

      GlobalMemoryLayout global = new GlobalMemoryLayout(globals);



      LinkedList<StackInstruction> insts = new LinkedList<>();
      for (Definition def : definitions.values()) {
         List<StackInstruction> defBody = def.compileToLIR(global);
         insts.addAll(defBody);
      }


      LinkedList<StackInstruction> body = createMain(global);

      Collections.reverse(body);
      for (StackInstruction inst : body) {
         insts.addFirst(inst); 
      }

      InstructionLayout layout = instLayout.build(insts, nondet);
      
      return new StackProgram(global, layout);
   }


   private LinkedList<StackInstruction> createMain(GlobalMemoryLayout global) {

      LinkedList<StackInstruction> body = new LinkedList<>();
      body.add(new Alloc(new Size(global.getSize(), "global")));
      body.addAll(globalMemorySetup(global));
      body.add(new Invoke(new UnresolvedNameLabel("stdlib", "fill_tables", ""), new Size(0, "")));
      
      body.add(new Invoke(new UnresolvedNameLabel("stdlib", "receive_packet", ""), new Size(0, "")));
      body.add(new Invoke(new UnresolvedNameLabel("", "main", ""), new Size(0, "")));
      body.add(new Pop());
      body.add(new Exit());
      return body;
   }

   private Collection<? extends StackInstruction> globalMemorySetup(GlobalMemoryLayout global) {
      LinkedList<StackInstruction> insts = new LinkedList<>();

      for (Segment s : global.getHeaders()) {
         String id = s.getPrefix() + "." + s.getName();
         insts.add(new Const(new Size(s.getType().getSize() - 2, id + " without validity bit, size field")));
         insts.add(new Const(new GlobalAddress(s.getAddress() + 1, id + ".size")));
         insts.add(new PutField());
      }

      return insts;
   }


   private void checkLinkage() {
      for (Declaration decl : declarations) {
        if(definitions.get(decl) == null) {
            throw new IllegalStateException("No implementation found for " + decl);
        }
      }
   }


   @Override
   public String toString() {
      return "Program [globalData=" + globals +  ", definitions=" + declarations + "]";
   };


}
