/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */

package p4query.applications.verification;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import p4query.ontology.providers.AppUI;
import p4query.ontology.providers.Application;
import p4query.ontology.providers.CLIArgs;
import p4query.ontology.Status;

import p4query.ontology.analyses.Verification;

public class VerifyApp implements Application {

    private final VerifyCommand cmd = new VerifyCommand();

    @Inject                      private GraphTraversalSource g;
    @Inject @Verification        private Provider<Status> v;
    @Inject @CLIArgs             private AppUI cli;

    @Override
    public VerifyCommand getUI(){
        return cmd;
    }

    @Override
    public Status run() throws IOException, TransformerException, InterruptedException {
        v.get();        
        
        return new Status();
    }

}
