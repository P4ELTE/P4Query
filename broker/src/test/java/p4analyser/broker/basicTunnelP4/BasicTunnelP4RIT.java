/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.broker.basicTunnelP4;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import p4analyser.broker.P4Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(Suite.class)
@SuiteClasses({BasicTunnelP4CallGraph.class})
public class BasicTunnelP4RIT {

    private static String fileName = "basic_tunnel.p4";
    private static List<String> analyses = Arrays.asList("CallGraph");

    @ClassRule
    public static P4Resource source = P4Resource.getP4Resource(fileName, analyses);

}

