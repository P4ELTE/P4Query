package p4analyser.broker;

import org.junit.BeforeClass;
import org.junit.rules.ExternalResource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tools.ant.taskdefs.Exit;

import java.util.List;

public class P4Resource extends ExternalResource {

    private static String fileName;
    private static List<String> analyses;  

    private static Boolean ref = false;
    private static P4Resource currentInstance;
    private static BaseITClass currentBaseITClass;

    public static P4Resource getP4Resource(String inFile, List<String> inAnalyses) {
        if (!ref || fileName != inFile || !analyses.containsAll(inAnalyses)) {
            currentInstance = new P4Resource(inFile, inAnalyses);

            if (ref) {
                try {
                    currentBaseITClass.postTest();
                } catch (Exception e) {
                    System.out.println(e);                    
                }
            }
            ref = true;
            currentBaseITClass = new BaseITClass();
            currentBaseITClass.preTests(fileName, analyses);
        }
        return currentInstance;
    }

    public P4Resource(String inFile, List<String> inAnalyses) {
        fileName = inFile;
        analyses = inAnalyses;
        System.out.println("create P4Resource: " + inFile + " " + inAnalyses);
    }

    public GraphTraversalSource getGraphTravSource() {
        return currentBaseITClass.g;
    }
}
