package p4query.applications.smc.hir.typing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class GlobalStruct implements Composite {

    private final LinkedHashMap<String, IRType> fields = new LinkedHashMap<>(); // stored in input order
    private final GraphTraversalSource g;
    private final IRType.SingletonFactory typeFactory;
    private final int size;

    public GlobalStruct(GraphTraversalSource g, IRType.SingletonFactory factory) {
        this.g = g;
        this.typeFactory = factory;
        fillFields();
        this.size = calcSize();
    }
    
    private void fillFields() {
        List<Map<String, Object>> ms = 
            g.V().has(Dom.Syn.V.CLASS, "ConstantDeclarationContext")
                .project("name", "type")
                .by(__.outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                    .inV()
                    .values("value"))
                .by(__.outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                    .inV())
                .toList();

        for (Map<String,Object> m : ms) {
            String name = (String) m.get("name");
            Vertex type = (Vertex) m.get("type");

            fields.put(name, typeFactory.create(type));
        }

    }

    @Override
    public String getName() {
        return "GLOBAL";
    }

    @Override
    public int getSize() {
        return size;
    }

    private int calcSize() {
        int s = 0;
        for (IRType type : fields.values()) {
           s += type.getSize() ;
        }

        return s;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }
    

    
}
