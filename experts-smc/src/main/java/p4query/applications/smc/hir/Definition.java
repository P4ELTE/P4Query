package p4query.applications.smc.hir;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

public interface Definition {

//    public List<Instruction> getBody() ;
    public String getNamespace()  ;
    public String getName(); 
    public LocalStruct getLocal() ;   
//    public LocalMemoryLayout getMemoryLayout() ;   
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global);
	public String addTemporary(IRType type);

    // In P4, actions can also scope the parameters of the parent control, which is
    // weird.
    // To achieve lexical scoping, I duplicate the parameters of the parent in the
    // action as well.
    public static LinkedHashMap<String, IRType>
 stealParamsFromParentControl(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory) {
        List<Vertex> ctl = 
            g.V(src).repeat(__.inE(Dom.SYN).outV())
                    .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))
                    .toList();

        if (ctl.isEmpty()) {
            return new LinkedHashMap<>();
        }

        ProcedureDefinition pdef = new ProcedureDefinition(g, ctl.get(0), typeFactory);

        // local.prependFields(pdef.local.getFields());
        return pdef.getLocal().getFields();

    }
}
