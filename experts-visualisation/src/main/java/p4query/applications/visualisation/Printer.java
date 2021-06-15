/**
 * Copyright 2020-2021, Eötvös Loránd University.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package p4query.applications.visualisation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.reflections.Reflections;

import p4query.applications.visualisation.GraphUtils.Label;
import p4query.ontology.Status;
import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.CallGraph;
import p4query.ontology.analyses.CallSites;
import p4query.ontology.analyses.ControlFlow;
import p4query.ontology.analyses.SymbolTable;
import p4query.ontology.analyses.SyntaxTree;
import p4query.ontology.providers.AppUI;
import p4query.ontology.providers.Application;
import p4query.ontology.providers.CLIArgs;

public class Printer implements Application {

    DrawCommand cmd = new DrawCommand();

    @Inject                      private GraphTraversalSource g; 
    @Inject @SyntaxTree          private Provider<Status> st;
    @Inject @AbstractSyntaxTree  private Provider<Status> ast;
    @Inject @SymbolTable         private Provider<Status> symtab;
    @Inject @CallGraph           private Provider<Status> cg;
    @Inject @ControlFlow         private Provider<Status> cfg;
    @Inject @CallSites           private Provider<Status> cs;
    @Inject @CLIArgs           private AppUI cli;

    @Override
    public DrawCommand getUI(){
        return cmd;
    }

    @Override
    public Status run() throws IOException, TransformerException, InterruptedException {
        System.out.println("visu: " + cli);
 	long startTimeApp = System.currentTimeMillis();
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
            throw new IllegalArgumentException("draw: Add one or more argument from the following: " + analysesMap.keySet());
        }

        Collection<Label> selection = new ArrayList<>();
        for (String str : cmd.names) {
            Class<? extends Annotation> a = analysesMap.get(str);
            selection.add(labels.get(a));
        }
        System.out.println("selection: " + selection);

        // parameters are validated, start invoking the dependencies

        for (String str : cmd.names) {
            Class<? extends Annotation> a = analysesMap.get(str);
            if(providers.get(a) == null)
                throw new IllegalArgumentException("No analyser found with name " + a);
            providers.get(a).get();
        }

        GraphUtils.printGraph(GraphUtils.subgraph(g, selection.toArray(new Label[selection.size()])), 
                              "proba", 
                              true, 
                              GraphUtils.Extension.SVG);
        
        long stopTimeApp = System.currentTimeMillis();
        System.out.println(String.format("Application complete. Time used: %s ms.", stopTimeApp - startTimeApp));
        return new Status();
    }

}
