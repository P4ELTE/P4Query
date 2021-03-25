package p4query.applications.smc.hir.exprs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Store;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.ontology.Dom;

public class LiteralExpression implements Expression {

    // TODO make this an enum
    private final String type;
    private final String value;
    CustomStorageReference storage;
    private int sizeHint;
    public LiteralExpression(GraphTraversalSource g, Vertex v, IRType.SingletonFactory typeFactory, Definition procDef, int sizeHint) {

        Map<String, Object> m = 
            g.V(v).outE(Dom.SYN).or(__.has(Dom.Syn.E.RULE, "INTEGER"))
                  .project("type", "value")
                  .by(__.values(Dom.Syn.E.RULE))
                  .by(__.inV().values("value"))
                  .next();
        this.type = (String) m.get("type");
        this.value = (String) m.get("value");

        if(!this.type.equals("INTEGER")){
            throw new IllegalArgumentException("Unknown literal type " + this.type + " with value " + this.value + " of vertex " + v);
        }

        if(sizeHint == -1){
            throw new IllegalArgumentException("Cannot instantiate literal with unknown size (type " + this.type + ", value " + this.value + ", vertex " + v);
        }

        IRType type = typeFactory.create(this.type + "_" + sizeHint, sizeHint, this);
        String localName = procDef.addTemporary(type);
        this.storage = new CustomStorageReference("TerminalNodeImpl", localName, type, this);
        this.sizeHint = sizeHint;
    }


    @Override
    public String toString() {
        return "LiteralExpression [type=" + type + ", value=" + value + "]";
    }

    @Override
    public String toP4Syntax() {
        return value;
    }

    @Override
    public StorageReference getStorageReference() {
        return storage;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        return this.storage.compileToLIR(local, global);
    }

    @Override
    public int getSizeHint() {
        return sizeHint;
    }
}
