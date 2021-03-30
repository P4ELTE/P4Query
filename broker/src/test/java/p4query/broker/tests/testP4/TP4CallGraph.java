/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.broker.tests.testP4;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.BeforeClass;
import java.util.Arrays;
import java.util.List;
import p4query.broker.P4Resource;

import p4query.ontology.Dom;

public class TP4CallGraph {

    private static String fileName = "src/main/resources/test.p4";
    private static List<String> analyses = Arrays.asList("CallGraph");
    private static GraphTraversalSource g;

    @BeforeClass
    public static void preTest() {
        P4Resource source = P4Resource.getP4Resource(fileName, analyses);
        try {
          g = source.getGraphTravSource();
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }    }
    
    @Test
    public void testEdgeNumber() {
        assertEquals(18, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
    }

    @Test
    public void testTableCallActionNumber() {
        assertEquals(4, g.V().has(Dom.Syn.V.CLASS , "TableDeclarationContext")
                    .outE().has(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                    .inV().has(Dom.Syn.V.CLASS, "ActionDeclarationContext")
                    .count().next().intValue());
    }

    @Test
    public void testControlCallTableNumber() {
        assertEquals(1, g.V().has(Dom.Syn.V.CLASS , "ControlDeclarationContext")
                    .outE().has(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                    .inV().has(Dom.Syn.V.CLASS, "TableDeclarationContext")
                    .count().next().intValue());
    }

    @Test
    public void testParserEdgeNumber1() {
        assertEquals(3, g.V().has(Dom.Syn.V.CLASS , "ParserDeclarationContext")
                    .outE().has(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                    .inV().has(Dom.Syn.V.CLASS, "ParserStateContext")
                    .count().next().intValue());
    }

    @Test
    public void testParserEdgeNumber2() {
        assertEquals(2, g.V().has(Dom.Syn.V.CLASS , "ParserStateContext")
                    .outE().has(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                    .inV().has(Dom.Syn.V.CLASS, "FunctionPrototypeContext")
                    .count().next().intValue());
    }

    @Test
    public void testActionFunctionNumber() {
        assertEquals(1, g.V().has(Dom.Syn.V.CLASS , "ActionDeclarationContext")
                    .outE().has(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                    .inV().has(Dom.Syn.V.CLASS, "FunctionPrototypeContext")
                    .count().next().intValue());
    }

    @Test
    public void testControlFunctionNumber2() {
        assertEquals(1, g.V().has(Dom.Syn.V.CLASS , "ControlDeclarationContext")
                    .outE().has(Dom.Call.ROLE, Dom.Call.Role.CALLS)
                    .inV().has(Dom.Syn.V.CLASS, "FunctionPrototypeContext")
                    .count().next().intValue());
    }
}
