package p4analyser;

import java.util.List;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Property;

import p4analyser.ontology.Dom;


public class FejtoroHp {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String remoteTraversalSourceName = args[2];

        GraphTraversalSource g = AnonymousTraversalSource.traversal()
                .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));

        System.out.println(analyse_2(g));

        System.out.println("OK");
    }

    public static List<? extends Property<Object>> analyse_2(GraphTraversalSource g) {
        return g.V().has(Dom.Syn.V.CLASS,"ConditionalStatementContext").and(
                __.out().has(Dom.Syn.V.VALUE, "if"),
                __.repeat(__.out()).until(__.has(Dom.Syn.V.CLASS, "ExpressionContext")).out().and(
                    __.repeat(__.out()).until(__.has(Dom.Syn.V.CLASS, "ExpressionContext")).and(
                        __.repeat(__.out()).until(__.has(Dom.Syn.V.VALUE, "hdr")),
                        __.out().has(Dom.Syn.V.VALUE, "."),
                        __.repeat(__.out()).until(__.has(Dom.Syn.V.VALUE, "ipv4"))
                    ),
                    __.out().has(Dom.Syn.V.VALUE, "."),
                    __.repeat(__.out()).until(__.has(Dom.Syn.V.VALUE, "isValid"))
                )
        ).properties(Dom.Syn.V.LINE).toList();
    }
    
}
