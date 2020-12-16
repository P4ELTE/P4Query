package p4analyser.experts.nrtask4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.yaml.snakeyaml.nodes.NodeId;

import p4analyser.ontology.Dom;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String remoteTraversalSourceName = args[2];

        GraphTraversalSource g = AnonymousTraversalSource.traversal()
                .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));

        List<Object> result = analyse(g, "ethernet", "etherType");

        System.out.println(result);
    }

    public static List<Object> analyse(GraphTraversalSource g, Object header, Object field) {
        return g.V().has(Dom.Syn.V.CLASS,"SelectExpressionContext")
            .and(
                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV()
                .and(
                    __.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, "hdr"),
                    __.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, header),
                    __.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, field)
                )
            )
            .outE(Dom.SYN)
            .has(Dom.Syn.E.RULE, "selectCaseList").inV().repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext"))
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression")
            .inV().repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.VALUE).toList();

    }

    public static List<Object> analyseWithOrdering(GraphTraversalSource g, Object header, Object field) {
        /*return g.V().has(Dom.Syn.V.CLASS,"SelectExpressionContext")
            .and(
                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV()
                .and(
                    __.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, "hdr").as("mainHeader"),

                    __.repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(
                            __.values(Dom.Syn.V.NODE_ID).is(P.gt(g.V().select("mainHeader").values(Dom.Syn.V.NODE_ID).toList().get(0)))
                        )
                        .has(Dom.Syn.V.VALUE, header).as("header"),

                    __.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(
                            __.values(Dom.Syn.V.NODE_ID).is(P.gt(g.V().select("header").values(Dom.Syn.V.NODE_ID).toList().get(0)))
                        )
                        .has(Dom.Syn.V.VALUE, field)
                )
            )
            .outE(Dom.SYN)
            .has(Dom.Syn.E.RULE, "selectCaseList").inV().repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext"))
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression")
            .inV().repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.VALUE).toList();*/

        return g.V().has(Dom.Syn.V.CLASS,"SelectExpressionContext")
            .and(
                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV().as("expressionLists")
                .and(
                    __.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, "hdr").as("mainHeader")
                    .and(
                        g.V().select("expressionLists")
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(
                            __.values(Dom.Syn.V.NODE_ID).is(P.gt(
                                g.V().has(Dom.Syn.V.NODE_ID, 3701).values(Dom.Syn.V.NODE_ID).toList().get(0)
                                //g.V().from("mainHeader").outE(Dom.SYN).inV().values(Dom.Syn.V.NODE_ID).toList().get(0)
                            ))
                        )
                        .has(Dom.Syn.V.VALUE, header).as("header")
                        .and(
                            g.V().select("expressionLists")
                            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                            .repeat(__.out(Dom.SYN))
                            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                            .filter(
                                __.values(Dom.Syn.V.NODE_ID).is(P.gt(
                                    g.V().has(Dom.Syn.V.NODE_ID, 3706).values(Dom.Syn.V.NODE_ID).toList().get(0)
                                    //g.V().  ("header").outE(Dom.SYN).inV().values(Dom.Syn.V.NODE_ID).toList().get(0)
                                ))
                            )
                            .has(Dom.Syn.V.VALUE, field)
                        )
                    )
                )
            )
            .outE(Dom.SYN)
            .has(Dom.Syn.E.RULE, "selectCaseList").inV().repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext"))
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression")
            .inV().repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.VALUE).toList();
    }
}
