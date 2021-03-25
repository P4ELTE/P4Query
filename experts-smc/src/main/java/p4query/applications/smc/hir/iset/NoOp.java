package p4query.applications.smc.hir.iset;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

// DELETE THIS
public class NoOp implements Instruction {
    private String vClass;
    private String comment;
    private Vertex src;

    NoOp(GraphTraversalSource g, Vertex v, String vClass) {
        this.src = v;
        this.vClass = vClass;
        if(vClass.equals("ParserStateContext")){
            String stateName = (String)
                g.V(v).outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                    .inV()
                    .values("value")
                    .next();
            comment = "state "+ stateName;
        } 

        if(vClass.equals("BlockStatementContext")){
            comment = "start of block";
        }
    }

    @Override
    public String toString() {
        return "NoOp [vClass=" + vClass + "]";
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment(comment));
        return insts;
    }

    @Override
    public Vertex getOrigin() {
        return src;
    }

}
