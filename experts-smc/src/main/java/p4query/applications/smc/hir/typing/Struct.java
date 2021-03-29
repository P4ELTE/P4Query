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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.ontology.Dom;

public class Struct implements Composite {
    private final String name;
    private int size;
    private final String origClass;
    private LinkedHashMap<String, IRType> fields = new LinkedHashMap<>(); // stored in input order
    private final GraphTraversalSource g;
    private final IRType.SingletonFactory typeFactory;

    Struct(GraphTraversalSource g, Vertex v, String origClass, IRType.SingletonFactory factory) {

        this.g = g;
        this.typeFactory = factory;
        this.origClass = origClass;

        this.name = (String)
            g.V(v).outE(Dom.SYMBOL)
                  .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                  .values("value")
                  .next();

        fillFields(v);
        this.size = calcSize();

        if(isP4Header()){
           LinkedHashMap<String, IRType> newFields = new LinkedHashMap<>();

           IRType boolT = typeFactory.create("BOOLEAN", 1, null);
           newFields.put("valid", boolT);
           IRType intT = typeFactory.create("INTEGER", 1, null);
           newFields.put("size", intT);

           newFields.putAll(fields);
           this.fields = newFields;
           this.size = calcSize();
        }
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }

    @Override
    public String getName(){
        return this.name;
    }
    @Override
    public int getSize(){
        return this.size;
    }

    public boolean isP4Header(){
        return origClass.equals("HeaderTypeDeclarationContext");
    }

    public boolean isP4Struct(){
        return origClass.equals("StructTypeDeclarationContext");
    }

    @Override
    public String toString() {
        return "Struct [name=" + name + ", origClass=" + origClass + ", size=" + size + ", fields=" + fields.keySet() + "]";
    }

    private void fillFields(Vertex v) {

        String fieldListLabel ;
        String fieldLabel ;
        if(origClass.equals("StructTypeDeclarationContext")){
            fieldListLabel = "structFieldList";
            fieldLabel = "structField";
        } else if(origClass.equals("HeaderTypeDeclarationContext"))  {
            fieldListLabel = "structFieldList"; // note: it is the same for headers 
            fieldLabel = "structField";
        } else {
            throw new IllegalArgumentException(
                String.format("Struct initialized with vertex %s of unknown class %s.", v, origClass));
        }

        List<Map<String, Object>> fieldTypeVerts = 
            g.V(v)  .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, fieldListLabel)
                            .inV())
                    .emit()
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, fieldLabel).inV()
                    .project("fieldName", "fieldType")
                    .by(__.outE(Dom.SYMBOL)
                          .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                          .values("value"))
                    .by(__.outE(Dom.SYMBOL)
                          .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                          // note: has_type points to basetypecontexts directly, but points to terminals in case of struct types
                          .optional(
                              __.inE(Dom.SYMBOL)
                                .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()))
                    .toList();

        for(Map<String,Object> map : fieldTypeVerts) {
           String name = (String) map.get("fieldName"); 
           Vertex type = (Vertex) map.get("fieldType"); 

           // note: this is a singleton factory, return a unique reference for each type
           //       in case of basetypecontexts, two vertex may denote the same type

           IRType typeObj = typeFactory.create(type);

           fields.put(name, typeObj);
        }
    }

    private int calcSize() {
        int s = 0;
        for (IRType type : fields.values()) {
           s += type.getSize() ;
        }

        return s;
    }
    
}
