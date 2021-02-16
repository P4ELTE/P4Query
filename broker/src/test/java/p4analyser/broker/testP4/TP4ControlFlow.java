package p4analyser.broker.testP4;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import p4analyser.ontology.Dom;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;
import p4analyser.broker.P4Resource;

public class TP4ControlFlow {
    
    private static String fileName = "test.p4";
    private static List<String> analyses = Arrays.asList("ControlFlow");
    private static GraphTraversalSource g;

    @BeforeClass
    public static void preTest() {
        P4Resource source = P4Resource.getP4Resource(fileName, analyses);
        g = source.getGraphTravSource();
    }
    
    @Test
    public void test1() {
        assertEquals(2-1, 1);
    }
}
