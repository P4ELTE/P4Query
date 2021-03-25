package p4query.applications.smc.hir.typing;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class BaseType implements IRType {

    // NOTE: the target representation stores integers (good). bits are also stored as integers (bad).
    public static int DEFAULT_INTEGER_SIZE = 1; 

    private final String name;
    private final int size;

    BaseType(GraphTraversalSource g, Vertex v, String typeType) {
        List<Object> subterms = 
            g.V(v)
             .outE(Dom.SYN)
             .order().by(Dom.Syn.E.ORD, Order.asc)
             .inV()
             .values("value")
             .toList();
        this.name = subterms.stream().map(o -> (String) o).collect(Collectors.joining(""));

        // TODO use gremlin choose
        if(g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "BIT").hasNext()){
            String sizeStr = (String)
                  g.V(v).outE(Dom.SYN)
                        .has(Dom.Syn.E.RULE, "INTEGER")
                        .inV()
                        .values("value")
                        .next();
            this.size = Integer.parseInt(sizeStr);
        } else if(g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "ERROR").hasNext()){
            this.size = 1;
        } else {
            throw new IllegalArgumentException(
                String.format("Unable to extract size from %s vertex %s covering '%s'.", typeType, v, this.name));
        }

	}

	@Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        return this.size;
    }
    

    @Override
    public String toString() {
        return "BaseType [name=" + name + ", size=" + size + "]";
    }

}
