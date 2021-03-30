/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.broker.tests.basicP4;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import p4query.ontology.Dom;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.BeforeClass;
import java.util.Arrays;
import java.util.List;
import p4query.broker.P4Resource;


public class BasicP4ControlFlow {
    
    private static String fileName = "src/main/resources/basic.p4";
    private static List<String> analyses = Arrays.asList("ControlFlow");
    private static GraphTraversalSource g;

    @BeforeClass
    public static void preTest() {
        P4Resource source = P4Resource.getP4Resource(fileName, analyses);
        try {
          g = source.getGraphTravSource();
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
    }

    @Test
    public void testEdgeNumber() {
        assertEquals(15, g.E().has(Dom.Cfg.E.ROLE, Dom.Cfg.E.Role.FLOW).count().next().intValue());
    }
}
