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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.computer.MessageScope.Global;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.externs.IsValid;
import p4query.applications.smc.hir.externs.MarkToDrop;
import p4query.applications.smc.hir.externs.MemCmp;
import p4query.applications.smc.hir.externs.MemCpy;
import p4query.applications.smc.hir.externs.PacketOut;
import p4query.applications.smc.hir.externs.PacketInExtract;
import p4query.applications.smc.hir.externs.PacketIn;
import p4query.applications.smc.hir.externs.PacketOutEmit;
import p4query.applications.smc.hir.externs.ReceivePacket;
import p4query.applications.smc.hir.externs.Subtract;
import p4query.applications.smc.hir.externs.TableIpv4Lpm;
import p4query.applications.smc.hir.externs.UpdateChecksum;
import p4query.applications.smc.hir.externs.V1ModelMain;
import p4query.applications.smc.hir.iset.Instruction;
import p4query.applications.smc.hir.typing.BaseType;
import p4query.applications.smc.hir.typing.GlobalStruct;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.Struct;
import p4query.applications.smc.lir.StackProgram;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Exit;
import p4query.applications.smc.lir.iset.IntraProcJumping;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.applications.smc.lir.typing.UnresolvedVertexLabel;
import p4query.ontology.Dom;

public class Program {
   private LinkedList<IRType> globals = new LinkedList<>();
   private LinkedList<Definition> definitions = new LinkedList<>();
   private InstructionList body;
   private GraphTraversalSource g;
   private IRType.SingletonFactory typeFactory;

   private InstructionLayout.Builder instLayout = new InstructionLayout.Builder();

   public Program(GraphTraversalSource g){
      this.g = g;
      this.typeFactory = new IRType.SingletonFactory(g);
      fillBody();
      fillStructTypes();
      fillProcedureDefinitions();
   }

   private void fillBody() {
      Vertex pkg = 
         g.V().hasLabel(Dom.SYN)
            .has(Dom.Syn.V.CLASS, "StartContext")
            .next();

  //    if(pkg.size() < 1) System.err.println("Warning: pkg.size() < 1");
  //    if(pkg.size() > 1) System.err.println("Warning: pkg.size() > 1");

  //    body = new InstructionList(g, pkg.get(0), typeFactory);
//      body = new InstructionList(g, pkg, typeFactory);
   }

   private void fillProcedureDefinitions() {
      // TODO it doesnt work with parsers because select expressions have to flows. this has to be fixed in control flow expert
      List<Vertex> cfgSources = 
         g.E().hasLabel(Dom.CFG)
         .has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.ENTRY)
         .outV()
         .or(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
             __.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"),
             __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"))
         .toList();

      for (Vertex src : cfgSources) {
         Definition d = new ProcedureDefinition(g, src, typeFactory, instLayout);
         definitions.add(d);
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
      globals.addFirst(new PacketOut(typeFactory));
      globals.addFirst(new PacketIn());

      for (Definition def : definitions) {
         globals.add(def.getLocal().restrictToOwnedFields());
      }
      GlobalMemoryLayout global = new GlobalMemoryLayout(globals);

      addExternDefinitions();
      addTables(global);
      addV1Model(global);
      addReceivePacket(global);

      LinkedList<StackInstruction> insts = new LinkedList<>();
      for (Definition def : definitions) {
         insts.addAll(def.compileToLIR(global));
      }

      StackInstruction[] body = {
         new Const(new GlobalAddress(global.lookupSegmentByName("packet_in").getAddress(), "packet_in")),
         new Invoke(new UnresolvedNameLabel("core", "receive_packet", ""), new Size(1, "")),
         new Pop(),
         new Invoke(new UnresolvedNameLabel("", "main", ""), new Size(0, "")),
         new Pop(),
         new Exit()
      };
      Collections.reverse(Arrays.asList(body));
      for (StackInstruction inst : body) {
         insts.addFirst(inst); 
      }


      InstructionLayout layout = instLayout.build(insts);
      
      return new StackProgram(global, layout);
   }

   private void addReceivePacket(GlobalMemoryLayout global) {
      definitions.addFirst(new ReceivePacket(instLayout));
   }

   private void addExternDefinitions() {
      definitions.addFirst(new MemCmp(instLayout));
      definitions.addFirst(new MemCpy(instLayout));
      definitions.addFirst(new Subtract(instLayout));
      definitions.addFirst(new UpdateChecksum(instLayout));
      definitions.addFirst(new MarkToDrop(instLayout));
      definitions.addFirst(new PacketInExtract(instLayout));
      definitions.addFirst(new PacketOutEmit(instLayout));
      definitions.addFirst(new IsValid(instLayout));

   }

   private void addTables(GlobalMemoryLayout memLayout) {
      definitions.addFirst(new TableIpv4Lpm(g, instLayout, typeFactory, memLayout, definitions));
   }

   private void addV1Model(GlobalMemoryLayout memLayout) {
      definitions.addFirst(new V1ModelMain(g, instLayout, memLayout, definitions));
   }


   @Override
   public String toString() {
      return "Program [globalData=" + globals +  ", definitions=" + definitions +", body=" + body + "]";
   };


}
