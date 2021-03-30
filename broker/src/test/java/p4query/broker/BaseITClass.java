/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.broker;

import java.util.List;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class BaseITClass {

    public  GraphTraversalSource g;
    private List<String> analysesNames;
    private static Boolean open; 
    private App broker;

    public void preTests(String file, List<String> inApps) {
        analysesNames = inApps; 

        String[] args = new String[analysesNames.size() + 3];
        args[0] = "test"; args[1] = file; args[2] = "-A";
        for (int i = 0; i < analysesNames.size(); i++) {
           args[i+3] = analysesNames.get(i);
       }

      try {
         broker = new App(args);
         broker.run();
         open = true;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
      
  public void postTest() throws Exception {
      if (open)
        broker.close();
    }

    public GraphTraversalSource getTraversalSource() throws Exception {
      return broker.getGraphTraversalSource(); 
    }

  }