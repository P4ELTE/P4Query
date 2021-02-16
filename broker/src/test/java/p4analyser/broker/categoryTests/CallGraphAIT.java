/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.broker.categoryTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import p4analyser.broker.basicP4.BasicP4CallGraph;
import p4analyser.broker.basicTunnelP4.BasicTunnelP4CallGraph;
import p4analyser.broker.testP4.TP4CallGraph;


@RunWith(Suite.class)
@SuiteClasses({BasicP4CallGraph.class, TP4CallGraph.class, BasicTunnelP4CallGraph.class})
public class CallGraphAIT {

}

