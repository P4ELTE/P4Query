package p4analyser.broker.tests.basicTunnelP4;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.BeforeClass;
import java.util.Arrays;
import java.util.List;
import p4analyser.broker.P4Resource;

import p4analyser.ontology.Dom;

public class BasicTunnelP4CallGraph {

    private static String fileName = "basic_tunnel.p4";
    private static List<String> analyses = Arrays.asList("CallGraph");
    private static GraphTraversalSource g;

    @BeforeClass
    public static void preTest() {
        P4Resource source = P4Resource.getP4Resource(fileName, analyses);
        g = source.getGraphTravSource();
    }
    
    @Test
    public void test2() {
        //assertEquals(1, 2-1);
        assertEquals(23, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
    }

    @Test
    public void test1() {
        //assertEquals(2-1, 1);
        assertEquals(23, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
    }
}
