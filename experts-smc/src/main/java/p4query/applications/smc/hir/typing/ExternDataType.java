package p4query.applications.smc.hir.typing;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class ExternDataType implements IRType {

    private final String name;

    ExternDataType(GraphTraversalSource g, Vertex v, String typeType) {
        this.name = (String)
            g.V(v).outE(Dom.SYMBOL)
                  .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                  .values("value")
                  .next();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return "ExternDataType [name=" + name + "]";
    }


}
