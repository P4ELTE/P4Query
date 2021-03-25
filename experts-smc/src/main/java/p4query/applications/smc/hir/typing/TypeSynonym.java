package p4query.applications.smc.hir.typing;

import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class TypeSynonym implements IRType {

    private final String name;
    private final int size;
    private final IRType targetType;

    TypeSynonym(GraphTraversalSource g, Vertex v, String typeType, IRType.SingletonFactory factory) {
        Map<String, Object> map = 
            g.V(v)
            .project("name", "targetType")
            .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV().values("value"))
            .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.ALIASES_TYPE).inV())
            .next();

        this.name = (String) map.get("name");
        this.targetType = factory.create((Vertex) map.get("targetType"));
        this.size = this.targetType.getSize();
	}

	@Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize(){
        return this.size;
    }

    @Override
    public String toString() {
        return "TypeSynonym [name=" + name + ", targetType=" + targetType + ", size=" + size + "]";
    }

}
