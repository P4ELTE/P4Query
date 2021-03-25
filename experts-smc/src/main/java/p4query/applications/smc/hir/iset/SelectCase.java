package p4query.applications.smc.hir.iset;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.exprs.LiteralExpression;
import p4query.applications.smc.hir.exprs.SelectKeyExpression;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

public class SelectCase implements Instruction {
    private ProcedureDefinition procDef;
    private IRType.SingletonFactory typeFactory;
    private Vertex src;
    private GraphTraversalSource g;
    private SelectKeyExpression expr; 

    SelectCase(GraphTraversalSource g, Vertex v, String vClass, IRType.SingletonFactory typeFactory,
            ProcedureDefinition procDef) {
        this.g = g;
        this.src = v;
        this.typeFactory = typeFactory;
        this.procDef = procDef;

        this.expr = (SelectKeyExpression) Expression.Factory.create(g, src, typeFactory, procDef, -1);

    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.addAll(expr.compileToLIR(local, global));

        return insts;
    }



    @Override
    public Vertex getOrigin() {
        return src;
    }

    
}
