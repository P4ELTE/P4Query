package p4query.applications.smc.hir.externs;

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

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.Segment;
import p4query.applications.smc.hir.typing.IRType;
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
    private InstructionLayout.Builder instLayout;
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> addrPerParamPerProc;

    public V1ModelMain(GraphTraversalSource g, InstructionLayout.Builder instLayout, GlobalMemoryLayout memLayout, List<Definition> definitions) {
        super("main", "v1model");
        this.instLayout = instLayout;

      List<Object> names  =
         g.V().has(Dom.Syn.V.CLASS, "InstantiationContext")
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
      this.addrPerParamPerProc = addrPerParamPerProc(memLayout, names2, definitions);
    }

    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {



        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("define " + name + "()"));

        for (Map.Entry<String, LinkedHashMap<String, Integer>> entry : addrPerParamPerProc.entrySet()) {
            String procName = entry.getKey();

            Set<String> pars = entry.getValue().keySet();
            String parsStr = String.join(", ", pars);
            insts.add(new Comment(procName + "(" + parsStr + ")"));

            for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
                insts.add(new Const(new GlobalAddress(entry2.getValue(), entry2.getKey())));
            }
            insts.add(new Invoke(new UnresolvedNameLabel("", procName, ""), new Size(pars.size(), parsStr)));
            insts.add(new Pop());

        }

        insts.add(new Const(new Int(0, getName() + " terminates with status OK")));
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));
        insts.add(new Comment(" "));

        instLayout.registerProc(this, insts.getFirst());

        return insts;
    }

   private static LinkedHashMap<String, LinkedHashMap<String, Integer>> addrPerParamPerProc(GlobalMemoryLayout memLayout,
         List<String> procNames, List<Definition> definitions) {
      LinkedHashMap<String, LinkedHashMap<String, Integer>> index = new LinkedHashMap<>();
      for (String procName : procNames) {
         for (Definition def : definitions) {
            if(!def.getName().equals(procName)) 
               continue;
            LinkedHashMap<String, Integer> parSizes = new LinkedHashMap<>();

            for (IRType type : def.getLocal().getFields().values()) {
               Segment segm = memLayout.lookupSegmentByName(type.getName());
               parSizes.put(type.getName(), segm.getAddress());
            }

            index.put(procName, parSizes);
            break;
         }
      }
      return index;
   }


}
