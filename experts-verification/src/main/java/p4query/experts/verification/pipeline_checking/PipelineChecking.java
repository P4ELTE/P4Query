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
package p4query.experts.verification.pipeline_checking;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;


public class PipelineChecking {
    
    ModifierChecking modifierChecker;

    public PipelineChecking (String parser, String modifier, String deparser) {
        modifierChecker = new ModifierChecking(modifier);
    }

    public void analyse(GraphTraversalSource g) {
        modifierChecker.analyse(g);
    }

    public String getAllCond() {
        return modifierChecker.getAllCond();
    }
}
