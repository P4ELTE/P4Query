/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.broker.tests.basicP4;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import p4query.ontology.Dom;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.BeforeClass;
import java.util.Arrays;
import java.util.List;
import p4query.broker.P4Resource;
import p4query.broker.tests.MainTestFile;
import p4query.broker.tests.generalTests.ControlFlowTestGeneral;


@RunWith(Suite.class)
@SuiteClasses({ControlFlowTestGeneral.class, BasicP4ControlFlow.Tests.class})
public class BasicP4ControlFlow extends MainTestFile {
    
  private static String fileName = "src/main/resources/basic.p4";
  private static List<String> analyses = Arrays.asList("ControlFlow");

  @BeforeClass
  public static void preTest() {
      P4Resource source = P4Resource.getP4Resource(fileName, analyses);
      try {
        g = source.getGraphTravSource();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
  }

  public static class Tests {

    @Test
    public void testEdgeNumber() {
        assertEquals(1,1);   //you can use g as the graphtraversalsourece!
    }

  }
}
