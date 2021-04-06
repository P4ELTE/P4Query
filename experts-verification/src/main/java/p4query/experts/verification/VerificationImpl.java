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
package p4query.experts.verification;

import javax.inject.Singleton;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.codejargon.feather.Provides;

import p4query.ontology.Status;
import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.CallGraph;
import p4query.ontology.analyses.ControlFlow;
import p4query.ontology.analyses.Verification;

import p4query.experts.verification.pipeline_checking.PipelineChecking;
import p4query.experts.verification.pipeline_checking.PreCalculations;


public class VerificationImpl 
{

    @Provides
    @Singleton
    @Verification
    public Status analyse(GraphTraversalSource g, @CallGraph Status t, @ControlFlow Status cfg, @AbstractSyntaxTree Status ast){
        System.out.println(Verification.class.getSimpleName() +" started.");

        PipelineChecking pipelineChecker = new PipelineChecking("MyParser", "MyIngress", "MyDeparser");

        PreCalculations.analyse(g);
        
        pipelineChecker.analyse(g);
        System.out.println("=============\nConditions:\n=============\n" + pipelineChecker.getAllCond());

        System.out.println(Verification.class.getSimpleName() + " complete.");

        return new Status();
    }
}
