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
package p4query.applications.smc.hir.externs.implem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.Segment;
import p4query.applications.smc.hir.exprs.CustomStorageReference;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.exprs.ListExpression;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.externs.ExternDeclaration;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.hir.typing.Struct;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class V1ModelMain extends ExternDefinition {
   public V1ModelMain(Declaration iface) throws UnableToLinkDeclaration {
      super(iface, "main", null);
      //TODO Auto-generated constructor stub
   }

   //   private LinkedHashMap<String, LinkedHashMap<String, Segment>> segmsOfParamsOfProcs;
   private CompilerState state;

   LinkedHashMap<String, List<Expression>> calleeSigs = new LinkedHashMap<>();

   public void init(CompilerState state) {
        this.state = state;

        this.state.setParentDecl(this.getDeclaration());

      List<Object> names =
         state.getG().V().has(Dom.Syn.V.CLASS, "InstantiationContext")
               .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").inV()
               .repeat(__.outE(Dom.SYN).order().by(Dom.Syn.E.ORD, Order.asc).inV())
               .until(__.has(Dom.Syn.V.CLASS, "ArgumentContext"))
               .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
               .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
               .repeat(__.outE(Dom.SYN).inV())
               .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
               .values("value")
               .toList();
      Collections.reverse(names);
      List<String> names2 = names.stream().map(o -> (String) o).collect(Collectors.toList());

//      this.segmsOfParamsOfProcs = segmsOfParamsOfProc(memLayout, names2, state.getDefinitions());

      // TODO this should be doable in linear time, but state.getDeclarations() is not in the right order
      for(String name : names2){
         for (Declaration def : state.getDeclarations()) {
            if(def.getName().equals(name)){
               LinkedHashMap<String, IRType> pars = def.getParameters().getFields();

               calleeSigs.put(def.getName(), new LinkedList<>());
               for (Map.Entry<String, IRType> par : pars.entrySet()) {

                  // note: here we are using par.getValue().getName() instead of par.getKey()
                  //       e.g. 'headers' instead of 'hdr'.
                  //       this is because we want to lookup fields of the global instance directly
                  //       e.g. headers.ethernet.valid instead of hdr.ethernet.valid.

                  calleeSigs.get(def.getName()).add(argAsGlobalPointer(par.getValue().getName(), par.getValue()));
               }
            }
         }
      }

// TODO delete this, redundant
//      state.getGlobals().add(getDeclaration().getParameters().restrictToOwnedVars());
//      state.getGlobals().add(getDeclaration().getTemps());
    }

    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(super.openDefinition());

        for (Map.Entry<String, List<Expression>> sig : calleeSigs.entrySet()) {
            String procName = sig.getKey();
            List<Expression> pars = sig.getValue();
            String parsStr = paramsToP4Str(pars);

            insts.add(new Comment(procName + "(" + parsStr + ")"));

            LocalMemoryLayout lml = 
               new LocalMemoryLayout(getDeclaration().getParameters());
            for (Expression exp : pars) {
               insts.addAll(exp.compileToLIR(lml, global));
            }
            insts.add(new Invoke(new UnresolvedNameLabel("", procName, ""), new Size(pars.size(), parsStr)));
            insts.add(new Pop());
        }

        StackInstruction exit0 = new Const(new Int(0, getDeclaration().getName() + " terminates with status OK"));
        insts.add(exit0);
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));
        insts.add(super.closeDefinition());

        state.getInstLayout().registerProc(getDeclaration(), insts.getFirst(), exit0);

        return insts;
    }

   private static String paramsToP4Str(List<Expression> pars) {
      List<String> ps = pars.stream().map(p -> p.toP4Syntax()).collect(Collectors.toList());
      return String.join(", ", ps);
   }

   private Expression argAsGlobalPointer(String argName, IRType argType){
      if(argType instanceof Composite){
         return createPointerList(argName, argType);
      } else {
         return new CustomStorageReference(state, "v1model::main", argName, argType, null);
      }
   }

   private Expression createPointerList(String argName, IRType argType) {
      Composite comp = (Composite) argType;
      LinkedHashMap<String, IRType> fields = comp.getFields();
      LinkedList<Expression> fieldPointers = new LinkedList<>();
      for (Map.Entry<String, IRType> field : fields.entrySet()) {
         fieldPointers.add(argAsGlobalPointer(argName + "." + field.getKey(), field.getValue()));
      }
      return new ListExpression(state, fieldPointers, comp);
   }

}
