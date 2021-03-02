package p4analyser.experts.tztask6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4analyser.ontology.Dom;

public class App {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String remoteTraversalSourceName = args[2];

        GraphTraversalSource g = AnonymousTraversalSource.traversal()
                .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));

        System.out.println(analyse(g, "ipv4", "dstAddr"));
    }

    // 3. Megoldás

    public static Map<Object, Object> analyse(GraphTraversalSource g, String header, String field) {
        Map<Object, Object> query = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext")
            .group()
            .by(__.values(Dom.Syn.V.NODE_ID))
            .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .filter(__.values(Dom.Syn.V.VALUE).is(P.neq(".")))
                    .values(Dom.Syn.V.NODE_ID, Dom.Syn.V.VALUE)
                    .fold())
            .next();

        List<Object> keyElementContextNodeIds = new ArrayList<Object>();

        for (Object nodeId : query.keySet()) {
            List<Object> valuesList = (List<Object>) query.get(nodeId);
            Map<String, Long> map = new HashMap<String, Long>();

            for (int i = 0; i < valuesList.size(); i += 2) {
                map.put((String) valuesList.get(i), (Long) valuesList.get(i + 1));
            }

            if (map.size() == 3 && map.containsKey("hdr") && map.containsKey(header) && map.containsKey(field)
                    && map.get("hdr") < map.get(header) && map.get(header) < map.get(field)) {
                keyElementContextNodeIds.add(nodeId);
            }
        }

        return
            (g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext")
                .filter(__.values(Dom.Syn.V.NODE_ID).is(P.within(keyElementContextNodeIds)))
                .group()
                .by(__.group()
                    .by(__.inE(Dom.SYN).outV().repeat(__.in(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TableDeclarationContext"))
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .values(Dom.Syn.V.VALUE))

                    .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .values(Dom.Syn.V.VALUE)))

                .by(__.inE(Dom.SYN).outV().inE(Dom.SYN).outV().inE(Dom.SYN).outV().outE(Dom.SYN).inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()

                    .outE(Dom.SYN).inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .filter(__.values(Dom.Syn.V.VALUE).is(P.neq(".")))
                    .values(Dom.Syn.V.VALUE)
                    .fold()
                )
            ).next();


        //TerminalNodes próbálása
        
        /*
         * System.out.println(
         * terminalNodes(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS,
         * "KeyElementContext").outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV())
         * );
         */

        /*Object compare = "hdr." + header + "." + field;

        System.out.println(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext")
                .filter(
                    terminalNodes(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV())
                    .is(P.eq(compare))
                )
                .values(Dom.Syn.V.NODE_ID)

        // terminalNodes(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS,
        // "KeyElementContext").outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV())
        );*/


        // Általánosítás előtti visszatérési érték.

        /*return g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "TableDeclarationContext")
                .filter(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList").inV().repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext")).values(Dom.Syn.V.NODE_ID)
                        .is(P.within(keyElementContextNodeIds)))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV().repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).values(Dom.Syn.V.VALUE).toList();*/
    }

    public static Object terminalNodes(GraphTraversal<Vertex, Vertex> g) {
        List<Object> objects =
            (
                g.repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .values(Dom.Syn.V.NODE_ID, Dom.Syn.V.VALUE)
                .fold()
            ).next();

        Map<Long, String> map = new HashMap<Long, String>();

        for (int i = 0; i < objects.size(); i += 2) {
            map.put((Long) objects.get(i + 1), (String) objects.get(i));
        }

        SortedSet<Long> keys = new TreeSet<Long>(map.keySet());

        StringBuilder result = new StringBuilder();

        for (Long key : keys) {
            result.append(map.get(key));
        }

        return result;

        //Próbálkozás, hogy gremlinnek megfelelő formátummal térjen vissza a függvény :(

        /*DefaultGraphTraversal<Element, Object> defaultGraphTraversal = new DefaultGraphTraversal<Element, Object>();

        defaultGraphTraversal.asAdmin().getBytecode().addStep(Symbols.values, Dom.Syn.V.VALUE);
        return defaultGraphTraversal.asAdmin()
                .addStep(new PropertiesStep<>(defaultGraphTraversal.asAdmin(), new PropertyKeyStep(defaultGraphTraversal), Dom.Syn.V.VALUE));
        
        //.addStep(new PropertiesStep<>(defaultGraphTraversal.asAdmin(), result, Dom.Syn.V.VALUE));
        */

        //return new DefaultGraphTraversal<>(); //new GraphTraversal<Element, Object>();
    }

    // 2. Megoldás
    
    public static List<Object> uglyAnalyse(GraphTraversalSource g) {
        GraphTraversal<Vertex, Map<Object, Object>> query = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "TableDeclarationContext")
            .group()
            .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .values(Dom.Syn.V.VALUE))
            .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                    .group()
                    .by(Dom.Syn.V.NODE_ID)
                    .by(__.repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(__.values(Dom.Syn.V.VALUE).is(P.neq(".")))
                        .valueMap(Dom.Syn.V.NODE_ID, Dom.Syn.V.VALUE).fold()));

        Map<Object, Object> tableDeclarationMap = query.next();

        List<Object> results = new ArrayList<Object>();

        for (Object tableName: tableDeclarationMap.keySet()) {
            Map<Object, Object> expressionMap = (Map<Object, Object>)tableDeclarationMap.get(tableName);

            for (Object expression: expressionMap.keySet()) {
                Map<String, Long> map = terminalNodeToMap((List<Object>) expressionMap.get(expression));

                if (map.size() == 3 && map.containsKey("hdr") && map.containsKey("ipv4") && map.containsKey("dstAddr") &&
                    map.get("hdr") < map.get("ipv4") && map.get("ipv4") < map.get("dstAddr")) {
                    results.add(tableName);
                }
            }
        }

        return results;
    }

    private static Map<String, Long> terminalNodeToMap(List<Object> terminalNodeImplList) {
        Map<String, Long> map = new HashMap<String, Long>();

        for (Object terminalNode: terminalNodeImplList) {
            Map<Object, Object> terminalNodeMap = (Map<Object, Object>)terminalNode;
            Object value = null;
            Object nodeId = null;

            for (Object terminalNodeMapKey: terminalNodeMap.keySet()) {
                List<Object> object = (List<Object>) terminalNodeMap.get(terminalNodeMapKey);

                if (terminalNodeMapKey.equals(Dom.Syn.V.VALUE)) {
                    value = object.get(0);
                }
                if (terminalNodeMapKey.equals(Dom.Syn.V.NODE_ID)) {
                    nodeId = object.get(0);
                }
            }
            map.put((String)value, (Long)nodeId);
        }

        return map;
    }

    // 1. Megoldás
    
    public static List<Object> theUglyAnalyse(GraphTraversalSource g) {
        List<Object> tableDeclarationContexts = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "TableDeclarationContext").values("nodeId").toList();
        List<Object> nodeIds = new Vector<Object>();

        System.out.println("tableDeclarationContexts: " + tableDeclarationContexts);

        for (int i = 0; i < tableDeclarationContexts.size(); ++i) {
            List<Object> keyElementContexts = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, tableDeclarationContexts.get(i))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                .values("nodeId").toList();

            System.out.println(i + ". keyElementContexts:" + keyElementContexts);

            for (int j = 0; j < keyElementContexts.size(); ++j) {
                List<Object> terminalNodeImpls = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, keyElementContexts.get(j))
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .values("nodeId").toList();

                System.out.println(i + "." + j + ". terminalNodeImpls:" + terminalNodeImpls);

                Map<String, Long> map = new HashMap<String, Long>();
                for (Object nodeId : terminalNodeImpls) {
                    String value = (String)g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, nodeId).values("value").toList().get(0);
                    if (!value.equals(".")) {
                        map.put(value, (Long)nodeId);
                    }
                }

                System.out.println(i + "." + j + ". map:" + map);

                if (terminalNodeImpls.size() == 5 && map.size() == 3 && map.containsKey("hdr") && map.containsKey("ipv4") && map.containsKey("dstAddr") &&
                    map.get("hdr") < map.get("ipv4") && map.get("ipv4") < map.get("dstAddr")) {
                    nodeIds.add(tableDeclarationContexts.get(i));
                }
            }
        }

        System.out.println("nodeIds: " + nodeIds);

        List<Object> result = new Vector<Object>();

        for (Object nodeId : nodeIds) {
            result.add(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, nodeId)
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .values("value").toList().get(0));
        }

        return result;
    }
}
