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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class EnumType implements Composite {

    private final String name;
    private final LinkedHashMap<String, IRType> fields; // stored in input order
    private SingletonFactory factory;

    EnumType(GraphTraversalSource g, Vertex v, String origClass, IRType.SingletonFactory factory) {
        this.factory = factory;
        this.name = (String)
            g.V(v).outE(Dom.SYMBOL)
                  .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                  .values("value")
                  .next();

        this.fields = fillFields(g, v);
	}

	private LinkedHashMap<String, IRType> fillFields(GraphTraversalSource g, Vertex v) {
        LinkedHashMap<String, IRType> fields = new LinkedHashMap<>();

        List<Object> fieldNames = 
            g.V(v)
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "identifierList")
                    .inV())
            .emit()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values("value")
            .toList();
        
        Collections.reverse(fieldNames);
        IRType enumFType = factory.create("enum-field-type", 1, null);
        for (Object fo : fieldNames) {
            fields.put((String) fo , enumFType);
        }
        return fields;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        return fields.size();
    }
    
    @Override
    public String toString() {
        return "EnumType [fields=" + fields + ", name=" + name + "]";
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }

}
