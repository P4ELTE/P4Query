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
package p4query.applications.smc.hir.typing;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class ExternDataType implements IRType {

    private final String name;

    ExternDataType(GraphTraversalSource g, Vertex v, String typeType) {
        this.name = (String)
            g.V(v).outE(Dom.SYMBOL)
                  .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                  .values("value")
                  .next();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return "ExternDataType [name=" + name + "]";
    }


}
