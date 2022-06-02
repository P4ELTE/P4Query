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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.Fraction;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.TableEntry;
import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.exprs.CustomStorageReference;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.exprs.LiteralExpression;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.TableDeclaration;
import p4query.applications.smc.hir.p4api.TableDefinition;
import p4query.applications.smc.hir.p4api.externs.ExternDeclaration;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.DerefTop;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Mul;
import p4query.applications.smc.lir.iset.Not;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.ProbabilisticInstruction;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Sub;
import p4query.applications.smc.lir.iset.Top;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;


public class TableApplicationImpl extends TableDefinition {


    private Map<String, LinkedList<StackInstruction>> actionCodes = new HashMap<>();

    private CompilerState state;

    private String[] actions;
    
    // TODO multiphase initialization should be eliminated. use e.g. builder?
    // TODO the action name should be stored in memory by FillTablesImpl as well.
    public TableApplicationImpl(Declaration iface, String[] entryActionNameMapping)
            throws UnableToLinkDeclaration {
        super(iface);
        this.actions = entryActionNameMapping;
    }

    public void init(CompilerState state) {
        this.state = state;
    }


    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(super.openDefinition());

        StackInstruction junction = new Const(new Int(0, getDeclaration().getName() + " terminates with status OK"));

// alternative 0:
//      String actionName = "ipv4_forward";
//      callAction("ipv4_forward");

// alternative 1:
//      addProbabilisticActionCalls(global, insts, junction);

// alternative 2:
//      addActionExceptOnMiss(global, insts, junction);

        LinkedList<StackInstruction> successCase = createSuccessCase(global);
        Map<String, LinkedList<StackInstruction>> actionCases = createActionCases(global, successCase);
 
        insts.addAll(lookup(global, actionCases));
 
        insts.add(new Comment("failure: no entries matched, action performed, leave hit bit at 0"));
        insts.add(new Goto(new UnresolvedInstructionLabel(junction, "last entry failed, go to junction")));
 
        insts.addAll(
            actionCases.values()
            .stream()
            .flatMap(c -> c.stream())
            .collect(Collectors.toList())); // flatten

        insts.addAll(successCase);

        insts.add(junction);

        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));
        insts.add(super.closeDefinition());

        state.getInstLayout().registerProc(getDeclaration(), insts.getFirst(), junction);

        return insts;

    }

    private LinkedList<StackInstruction> lookup(GlobalMemoryLayout global,  Map<String, LinkedList<StackInstruction>> actionCases){
        LinkedList<StackInstruction> insts = new LinkedList<>();
        TableDeclaration decl = (TableDeclaration) getDeclaration();

        LocalMemoryLayout localMem = new LocalMemoryLayout(decl.getParameters());

        String tname = decl.getName();
        List<Expression> keys = decl.getKeys();
        int numKeys = keys.size();

        insts.add(new Comment("keys"));
        int numEntries = actions.length;
        for (int i = 0; i < numEntries; i++) {
            insts.add(new Comment(String.format("Match entry %d of %s", i, tname)));
            insts.add(new Const(new Int(1, "initial true, every key will binary-and it with the results")));
            for (int j = 0; j < numKeys; j++) {
                Expression key = keys.get(j);
                int keySize = key.getTypeHint().getSize();
 
                insts.add(new Comment(String.format("Match key %s entry %d of %s", key.toP4Syntax(), i, tname)));
 
                insts.addAll(key.compileToLIR(localMem, global));
 
                String entryId = tname + "."+ FillTablesImpl.entrySelector(i,j);
                Integer addr = global.lookupSegmentByName(entryId).getAddress();
                insts.add(new Const(new GlobalAddress(addr, entryId)));
 
                insts.add(new Const(new Size(keySize, entryId)));
                insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcmp", ""), new Size(3, "src, dst, length")));
 
                insts.add(new Mul());
            }
            insts.add(new Not());
            insts.add(new IfEq(new UnresolvedInstructionLabel(actionCases.get(actions[i]).getFirst(),
                                                              String.format("if all memcmp returned 1, i.e. if entry %d matched, jump to action %s", i , actions[i]))));
        }

        return insts;
    }

    private Map<String, LinkedList<StackInstruction>> createActionCases(GlobalMemoryLayout global, LinkedList<StackInstruction> successCase) {
        Map<String, LinkedList<StackInstruction>> insts = new HashMap<>();
        LinkedHashSet<String> anames = new LinkedHashSet<>(Arrays.asList(actions));
        for (String aname : anames) {
           LinkedList<StackInstruction> ainsts = new LinkedList<>();
           ainsts.addAll(callAction(aname, global));
           ainsts.add(new Goto(new UnresolvedInstructionLabel(successCase.getFirst(), "success case")));
           insts.put(aname, ainsts);
        }
        return insts;
    }

    private LinkedList<StackInstruction> createSuccessCase(GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("success: switch on the hit bit"));
        insts.add(new Const(new Int(1, "new value of hit bit")));
        String hitField = getDeclaration().getName() + ".hit";
        insts.add(
          new Const(
            new GlobalAddress(
              global.lookupSegmentByName(hitField).getAddress(), hitField)));
        insts.add(new PutField());
        return insts;
    }

    private void addActionExceptOnMiss(GlobalMemoryLayout global,
            LinkedList<StackInstruction> insts, StackInstruction junction) {
        TableDeclaration decl = (TableDeclaration) getDeclaration();

        LinkedList<String> ls = new LinkedList<>(decl.getActionNames());
        ls.remove("on_miss");
        if(ls.size() != 1){
            throw new IllegalStateException("Too many or too few actions in " + ls + ", expected 1.");
        }

        insts.add(new Comment("table calls " + ls.get(0)));
        insts.addAll(callAction(ls.get(0), global));
        insts.add(new Goto(new UnresolvedInstructionLabel(junction, "junction")));
    }

    private void addProbabilisticActionCalls(GlobalMemoryLayout global,
            LinkedList<StackInstruction> insts, StackInstruction junction) {
        ProbabilisticInstruction.Builder pb = new ProbabilisticInstruction.Builder();

        TableDeclaration decl = (TableDeclaration) getDeclaration();

        // TODO delete this
//        for (int i = 0; i < 10; i++) {
//            actionNames.add("ipv4_forward");
//        }

        // if there is an odd number of actions, we count with even and the first action will get one more part
        int n = decl.getActionNames().size(); 
        int denom;
        int numer;

        if(n == 1){
            denom = 1;
            numer = 1;
        } if(n % 2 == 0){
            denom = n; 
            numer = 1;
        } else {
            denom = n + 1;
            numer = 2; 
        }

        LinkedList<StackInstruction> allActInsts = new LinkedList<>();

        for (String actionName : decl.getActionNames()) {

            LinkedList<StackInstruction> actInsts;

            // NOTE this does memoization to support reusing the same code, even if the same action is called
//            if(actionCodes.containsKey(actionName)){
//                actInsts = actionCodes.get(actionName);
//            } else {
                actInsts = callAction(actionName, global);
                actInsts.add(new Goto(new UnresolvedInstructionLabel(junction, "junction")));

                allActInsts.addAll(actInsts);
//           }

            pb.addInstruction(Fraction.getReducedFraction(numer, denom), 
                            new Goto(new UnresolvedInstructionLabel(actInsts.getFirst(), actionName)));
            numer = 1;
        }

        insts.add(pb.build());
        insts.addAll(allActInsts);
    }

    private
    LinkedHashMap<String, LinkedHashMap<String, Integer>> 
    addrPerOwnParamPerAct(GlobalMemoryLayout memLayout, LinkedList<Declaration> decls, List<String> actionNames) {

        LinkedHashMap<String, LinkedHashMap<String, Integer>> addrPerOwnParamPerAct = new LinkedHashMap<>();
        for (String act : actionNames) {
            for (Declaration def : decls) {
                if(!def.getName().equals(act)) 
                    continue;

                LinkedHashMap<String, IRType> typePerParam  =
                    def.getParameters().restrictToOwnedParameters().getFields();
                
                LinkedHashMap<String, Integer> addrPerParam  = new LinkedHashMap<>();
                for (Map.Entry<String, IRType> entry : typePerParam.entrySet()) {
                    Integer addr = memLayout.lookupSegmentByName(act + "." + entry.getKey()).getAddress();
                    addrPerParam.put(entry.getKey(), addr);
                }
                addrPerOwnParamPerAct.put(act, addrPerParam);
            }
        }
        return addrPerOwnParamPerAct;
    }


    // TODO parameterization should be separated
    private LinkedList<StackInstruction> callAction( String actionName, GlobalMemoryLayout global) {

        TableDeclaration decl = (TableDeclaration) getDeclaration();
        LinkedList<StackInstruction> insts = new LinkedList<>();


        LinkedHashMap<String, LinkedHashMap<String, Integer>> addrPerOwnParamPerAct = 
            addrPerOwnParamPerAct(global, state.getDeclarations(), decl.getActionNames());

        LinkedHashMap<String, Integer> paramPerProc = addrPerOwnParamPerAct.get(actionName);
        if(paramPerProc == null){
            throw new IllegalStateException(
                String.format("Could not find declaration of action %s when compiling definition of table %s", 
                    actionName, getDeclaration()));

        }

        Set<String> pars = new LinkedHashSet<>(getDeclaration().getParameters().getFields().keySet());

        pars.addAll(paramPerProc.keySet());

        String parsStr = String.join(", ", pars);
        insts.add(new Comment(actionName + "(" + parsStr + ")"));

        int i = 0;
        for (String parentPar : getDeclaration().getParameters().getFields().keySet()) {
            insts.add(new Load(new LocalAddress(i, parentPar)));
            ++i;
        }

        for (Map.Entry<String, Integer> entry : paramPerProc.entrySet()) {
            insts.add(new Const(new GlobalAddress(entry.getValue(), entry.getKey())));
        }

        insts.add(new Invoke(new UnresolvedNameLabel("", actionName, ""), new Size(pars.size(), parsStr)));

        insts.add(new Pop());

        actionCodes.put(actionName, insts);

        return insts;
    }
}
