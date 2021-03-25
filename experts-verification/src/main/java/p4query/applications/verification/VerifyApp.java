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
