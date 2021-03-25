package p4query.applications.smc.hir.externs;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.exprs.StorageReference;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.hir.typing.IRType.SingletonFactory;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.ProbabilisticInstruction;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class TableIpv4Lpm extends ExternDefinition {

    private Builder instLayout;

    private List<Expression> keys = new LinkedList<>();
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> addrPerOwnParamPerAct = new LinkedHashMap<>();

    private List<String> actionNames;
    private Map<String, LinkedList<StackInstruction>> actionCodes = new HashMap<>();

    public TableIpv4Lpm(GraphTraversalSource g, Builder instLayout, SingletonFactory typeFactory,
			GlobalMemoryLayout memLayout, LinkedList<Definition> definitions) {

        super("ipv4_lpm", null);
        this.instLayout = instLayout;

        Vertex src = 
            g.V().has(Dom.Syn.V.CLASS, "TableDeclarationContext")
                .filter(__.outE(Dom.SYMBOL)
                        .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                        .has(Dom.Syn.V.VALUE, this.name))
                .next();

        LinkedHashMap<String, IRType> parentPars = Definition.stealParamsFromParentControl(g, src, typeFactory);
        for (Map.Entry<String, IRType> m : parentPars.entrySet()) {
            local.appendField(m.getKey(), m.getValue());
        }

        List<Vertex> keyVerts = 
            g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList")
                    .inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TablePropertyContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "keyElementList").inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression")
                    .inV()
                    .toList();

        List<Object> actionNames0 = 
            g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList")
                    .inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TablePropertyContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "actionList").inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "ActionRefContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name")
                    .inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .values("value")
                    .toList();
        actionNames = actionNames0.stream().map(o -> (String) o).collect(Collectors.toList());
        Collections.reverse(actionNames);

        fillAddrPerOwnParamPerAct(memLayout, definitions, actionNames);

        for (Vertex k : keyVerts) {
            // TODO better size hint? (in case it's not a storage referece)
            keys.add(Expression.Factory.create(g, k, typeFactory, this, -1));
            
        }
    }


    private void fillAddrPerOwnParamPerAct(GlobalMemoryLayout memLayout, LinkedList<Definition> definitions, List<String> actionNames) {
        for (String act : actionNames) {
            for (Definition def : definitions) {
                if(!def.getName().equals(act)) 
                    continue;

                LinkedHashMap<String, IRType> typePerParam  =
                    def.getLocal().restrictToOwnedParameters().getFields();
                
                LinkedHashMap<String, Integer> addrPerParam  = new LinkedHashMap<>();
                for (Map.Entry<String, IRType> entry : typePerParam.entrySet()) {
                    Integer addr = memLayout.lookupSegmentByName(act + "." + entry.getKey()).getAddress();
                    addrPerParam.put(entry.getKey(), addr);
                }
                this.addrPerOwnParamPerAct.put(act, addrPerParam);
            }
        }
    }


    // TODO this will need a new unresolvedlabel, for jumping to instructions


	@Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {
        LocalMemoryLayout localMem = new LocalMemoryLayout(local.getFields());

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("define " + name + "()"));

        insts.add(new Comment("keys"));
        for (Expression key : keys) {
            insts.addAll(key.compileToLIR(localMem, global));
        }

        insts.add(new Comment("lpm"));
        insts.add(new Comment("TODO"));

//        String actionName = "ipv4_forward";
//        callAction("ipv4_forward");
        StackInstruction junction = new Const(new Int(0, getName() + " terminates with status OK"));

        ProbabilisticInstruction.Builder pb = new ProbabilisticInstruction.Builder();

        // TODO delete this
        for (int i = 0; i < 10; i++) {
            actionNames.add("ipv4_forward");
        }

        // if there is an odd number of actions, we count with even and the first action will get one more part
        int n = actionNames.size(); 
        int denom = n % 2 == 0 ? n : n + 1;
        int numer = 2; 

        LinkedList<StackInstruction> allActInsts = new LinkedList<>();

        for (String actionName : actionNames) {

            LinkedList<StackInstruction> actInsts;

            // NOTE this does memoization to support reusing the same code, even if the same action is called
//            if(actionCodes.containsKey(actionName)){
//                actInsts = actionCodes.get(actionName);
//            } else {
                actInsts = callAction(actionName);
                actInsts.add(new Goto(new UnresolvedInstructionLabel(junction, "junction")));

                allActInsts.addAll(actInsts);
 //           }

            pb.addInstruction(Fraction.getReducedFraction(numer, denom), 
                              new Goto(new UnresolvedInstructionLabel(actInsts.getFirst(), actionName)));
            numer = 1;
        }

        insts.add(pb.build());
        insts.addAll(allActInsts);

        insts.add(junction);

        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));
        insts.add(new Comment(" "));
        
        instLayout.registerProc(this, insts.getFirst());

        return insts;
    }

    // TODO parameterization should be separated
    private LinkedList<StackInstruction> callAction(String actionName) {

        LinkedList<StackInstruction> insts = new LinkedList<>();

        LinkedHashMap<String, Integer> paramPerProc = addrPerOwnParamPerAct.get(actionName);

        Set<String> pars = new LinkedHashSet<>(local.getFields().keySet());
        pars.addAll(paramPerProc.keySet());
        String parsStr = String.join(", ", pars);
        insts.add(new Comment(actionName + "(" + parsStr + ")"));

        int i = 0;
        for (String parentPar : local.getFields().keySet()) {
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
