/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.applications.tests;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.reflections.Reflections;

import p4query.ontology.providers.AppUI;
import p4query.ontology.providers.Application;
import p4query.ontology.providers.CLIArgs;
import p4query.applications.visualisation.GraphUtils.Label;
import p4query.ontology.Status;
import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.CallGraph;
import p4query.ontology.analyses.CallSites;
import p4query.ontology.analyses.ControlFlow;
import p4query.ontology.analyses.SymbolTable;
import p4query.ontology.analyses.SyntaxTree;


public class TApp implements Application {

    TCommand cmd = new TCommand();

    @Inject                      private GraphTraversalSource g; 
    @Inject @SyntaxTree          private Provider<Status> st;
    @Inject @AbstractSyntaxTree  private Provider<Status> ast;
    @Inject @SymbolTable         private Provider<Status> symtab;
    @Inject @CallGraph           private Provider<Status> cg;
    @Inject @ControlFlow         private Provider<Status> cfg;
    @Inject @CallSites           private Provider<Status> cs;
    @Inject @CLIArgs             private AppUI cli;

    @Override
    public TCommand getUI(){
        return cmd;
    }   
    
    public GraphTraversalSource getGraphTraversalSource() {
        return g;
    }

    @Override
    public Status run() throws IOException, TransformerException, InterruptedException {
        Map<Class<? extends Annotation>, Provider<Status>> providers = new HashMap<>();
        providers.put(SyntaxTree.class, st);
        providers.put(AbstractSyntaxTree.class, ast);
        providers.put(SymbolTable.class, symtab);
        providers.put(CallGraph.class, cg);
        providers.put(ControlFlow.class, cfg);
        providers.put(CallSites.class, cs);

        Map<Class<? extends Annotation>, Label> labels = new HashMap<>();
        labels.put(SyntaxTree.class, Label.SYN);
        labels.put(AbstractSyntaxTree.class, Label.SEM);
        labels.put(SymbolTable.class, Label.SYMBOL);
        labels.put(CallGraph.class, Label.CALL);
        labels.put(ControlFlow.class, Label.CFG);
        labels.put(CallSites.class, Label.SITES);

        Reflections reflections = new Reflections("p4query.ontology.analyses");
        Set<Class<? extends Annotation>> analyses = 
            reflections.getSubTypesOf(Annotation.class);
        Map<String, Class<? extends Annotation>> analysesMap = 
            analyses.stream()
                    .collect(Collectors.toMap(c -> c.getSimpleName(), c -> c));

        if(cmd.names == null || cmd.names.isEmpty()){
            throw new IllegalArgumentException("test: Add one or more argument from the following: " + analysesMap.keySet());
        }
        for (String str : cmd.names) {
            Class<? extends Annotation> a = analysesMap.get(str);
            providers.get(a).get();
        }
        
        return new Status();
    }

}
