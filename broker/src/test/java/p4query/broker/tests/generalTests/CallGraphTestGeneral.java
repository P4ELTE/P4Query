package p4query.broker.tests.generalTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import p4query.broker.tests.MainTestFile;
import p4query.ontology.Dom;

public class CallGraphTestGeneral extends MainTestFile {
  
  @Test
  public void test1() {
    System.out.println("call graph - general");
    assertEquals(18, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());

  }
}
