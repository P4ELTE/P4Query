/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.broker.suites.resourceSuites;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import p4query.broker.P4Resource;
import java.util.Arrays;
import java.util.List;

import p4query.broker.tests.basicP4.*;

@RunWith(Suite.class)
@SuiteClasses({BasicP4CallGraph.class, BasicP4ControlFlow.class})
public class BasicP4RIT {

    private static String fileName = "src/main/resources/basic.p4";
    private static List<String> analyses = Arrays.asList("ControlFlow", "CallGraph");

    @ClassRule
    public static P4Resource source = P4Resource.getP4Resource(fileName, analyses);

}

