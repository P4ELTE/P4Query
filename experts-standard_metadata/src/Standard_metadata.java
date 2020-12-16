package p4analyser.experts.controlflow;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class Standard_metadata 
{
    public static void main( String[] args )
    {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String remoteTraversalSourceName = args[2];

//        Graph graph = TinkerGraph.open();
//        GraphTraversalSource g = graph.traversal();
        GraphTraversalSource g = 
            AnonymousTraversalSource
                    .traversal()
                    .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));

        ControlFlowAnalysis.analyse(g);
    }
}

public static void actionCheck (GraphTraversalSource g)
{
    List<? extends Property<Object>> stndrd_mtdt = getNodeId(g, "standard_metadata", "ActionDeclarationContext")

    if(stndrd_mtdt.size() > 0)
    {
        System.out.println(stndrd_mtdt.get(stndrd_mtdt.size()-1));
    } else {
        System.out.println("The following node with the expression value does not exists!")
    }
}

public static List <? extends Property<Object>> getNodeId(GraphTraversalSource g)
{
    return g.V().has(Dom.Syn.V.CLASS, "ActionDeclarationContext").and(
        .repeat(_.out()).until().(_.has(Dom.Syn.V.VALUE, "standard_metadata"))
    ).properties(Dom.Syn.V.LINE).toList();
}