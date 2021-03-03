/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.broker.suites.analysisSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import p4analyser.broker.tests.basicP4.BasicP4ControlFlow;
import p4analyser.broker.tests.testP4.TP4ControlFlow;


@RunWith(Suite.class)
@SuiteClasses({BasicP4ControlFlow.class, TP4ControlFlow.class})
public class ControlFlowAIT {

}

