/**
 * Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
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
 *
 * Author: Dániel Lukács, 2022
 */
package p4query.applications.smc.hir.typing;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class BaseType implements IRType {

    // NOTE: the target representation stores integers (good). bits are also stored as integers (bad).
    public static int DEFAULT_INTEGER_SIZE = 1; 

    private final String name;
    private final int size;

    BaseType(GraphTraversalSource g, Vertex v, String typeType) {
        List<Object> subterms = 
            g.V(v)
             .outE(Dom.SYN)
             .order().by(Dom.Syn.E.ORD, Order.asc)
             .inV()
             .values("value")
             .toList();

        String name0 = subterms.stream().map(o -> (String) o).collect(Collectors.joining(""));
        this.name = name0.replace("\\>", "").replace("\\<", "_");

        if(g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "BIT").hasNext()){
            String sizeStr = (String)
                  g.V(v).outE(Dom.SYN)
                        .has(Dom.Syn.E.RULE, "INTEGER")
                        .inV()
                        .values("value")
                        .next();
            this.size = Integer.parseInt(sizeStr);
        } else if(g.V(v).outE(Dom.SYN).has(Dom.Syn.E.RULE, "ERROR").hasNext()){
            this.size = 1;
        } else if(this.name.equals("bool")){
            this.size = 1;
        } else {
            throw new IllegalArgumentException(
                String.format("Unable to extract size from %s vertex %s covering '%s'.", typeType, v, this.name));
        }

	}

	@Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        return this.size;
    }
    

    @Override
    public String toString() {
        return "BaseType [name=" + name + ", size=" + size + "]";
    }

}
