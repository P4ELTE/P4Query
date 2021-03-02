package p4analyser;

import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;


import p4analyser.ontology.Dom;


public class FejtoroHp {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String remoteTraversalSourceName = args[2];

        GraphTraversalSource g = AnonymousTraversalSource.traversal()
                .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));

        List<Object> list = analyse(g, "ipv4");
        System.out.println(getPropertyFromList(Dom.Syn.V.LINE, list));
        System.out.println(getControl(g, getPropertyFromList(Dom.Syn.V.NODE_ID, list)));
    }

    public static List<Object> analyse(GraphTraversalSource g, String headerName) {
        return g.V().has(Dom.Syn.V.CLASS,"ConditionalStatementContext").and(
                __.out().has(Dom.Syn.V.VALUE, "if"),
                __.repeat(__.out()).until(__.has(Dom.Syn.V.CLASS, "ExpressionContext")).out().and(
                    __.repeat(__.out()).until(__.has(Dom.Syn.V.CLASS, "ExpressionContext")).and(
                        __.repeat(__.out()).until(__.has(Dom.Syn.V.VALUE, "hdr")),
                        __.out().has(Dom.Syn.V.VALUE, "."),
                        __.repeat(__.out()).until(__.has(Dom.Syn.V.VALUE, headerName))
                    ),
                    __.out().has(Dom.Syn.V.VALUE, "."),
                    __.repeat(__.out()).until(__.has(Dom.Syn.V.VALUE, "isValid"))
                )
        ).properties(Dom.Syn.V.NODE_ID, Dom.Syn.V.LINE).value().toList();
    }

    public static List<Object> getControl(GraphTraversalSource g, List<Object> nodeIds) {
        List<Object> result = new ArrayList<Object>();
        for(int i = 0; i < nodeIds.size(); i++){
            List<Object> subresult = g.V().has(Dom.Syn.V.NODE_ID, nodeIds.get(i)).repeat(__.in()).until(
                __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext")).out().has(Dom.Syn.V.CLASS,"ControlTypeDeclarationContext")
                    .out().has(Dom.Syn.V.CLASS, "NameContext").repeat(__.out()).times(3)
                    .properties(Dom.Syn.V.VALUE).value().unfold().toList();
        
            result.addAll(subresult);
        }

        return result;
    }

    public static List<Object> getPropertyFromList(String prop, List<Object> propertyList) {
        List<Object> ret = new ArrayList<Object>();
        int mod = prop == Dom.Syn.V.LINE ? 0 : 1;
        for(int i = 0; i < propertyList.size(); i++) {
            if(i % 2 == mod) {
                ret.add(propertyList.get(i));
            }
        }
        return ret;
    }
    
}
