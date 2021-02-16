/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.broker.testP4;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import p4analyser.broker.P4Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(Suite.class)
@SuiteClasses({TP4CallGraph.class, TP4ControlFlow.class})
public class TP4RIT {

    private static String fileName = "test.p4";
    private static List<String> analyses = Arrays.asList("CallGraph", "ControlFlow");

    @ClassRule
    public static P4Resource source = P4Resource.getP4Resource(fileName, analyses);

}

