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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.exprs.Expression;
import p4query.ontology.Dom;

public interface IRType {
    public String getName();
    public int getSize();

    // TODO this is full of code duplication
    public static class SingletonFactory {
        private Map<Vertex, String> typeNames = new HashMap<>();
        private Map<String, IRType> typeObjs = new HashMap<>();
        private Map<String, GenType> customTypes = new HashMap<>();
        private GraphTraversalSource g;
        public SingletonFactory(GraphTraversalSource g) {
            this.g = g;
        }

        public GenType create(String name, int size, Expression origin){
            if(customTypes.containsKey(name)){
                GenType type = customTypes.get(name);
                if(type.getSize() != size)
                    throw new IllegalStateException("Type already stored, but it has a different size.");

                return type;
            }
            GenType type = new GenType(name, size, origin);
            customTypes.put(name, type);
            return type;
        }
        
        public IRType create(Vertex v){
            if(typeNames.containsKey(v))
                return  typeObjs.get(typeNames.get(v));

            String typeType = (String) g.V(v).values(Dom.Syn.V.CLASS).next();

            switch(typeType){
                case "StructTypeDeclarationContext": // intentional fallthrough
                case "HeaderTypeDeclarationContext": 
                    return handleStructAndHeader(v, typeType);
                case "TypedefDeclarationContext": 
                    return handleTypedef(v, typeType);
                case "BaseTypeContext": 
                    return handleBaseType(v, typeType);
                case "ExternDeclarationContext": 
                    return handleExternDataType(v, typeType);
                default: 
                    throw new IllegalArgumentException(
                        String.format("Type initialized with vertex %s of unknown class %s.", v, typeType));
            }
        }

        private IRType handleStructAndHeader(Vertex v, String typeType) {
            String typeName = (String)
                g.V(v).outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                    .values("value")
                    .next();

            if(typeObjs.containsKey(typeName)){
                IRType typeObj2 = typeObjs.get(typeName);
                typeNames.put(v, typeName);
                return typeObj2;
            }
            IRType typeObj = new Struct(g,v, typeType, this);
            registerTypeObjToVertex(v, typeObj);
            return typeObj;
        }

        private IRType handleTypedef(Vertex v, String typeType){
            String typeName = (String)
                g.V(v).outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                    .inV()
                    .values("value")
                    .next();

            if(typeObjs.containsKey(typeName)){
                IRType typeObj2 = typeObjs.get(typeName);
                typeNames.put(v, typeName);
                return typeObj2;
            }

            IRType typeObj = new TypeSynonym(g, v, typeType, this);
            registerTypeObjToVertex(v, typeObj);
            return typeObj;

        }

        // note: this is the same as handleStructAndHeader except for the initialization
        private IRType handleExternDataType(Vertex v, String typeType) {
            String typeName = (String)
                g.V(v).outE(Dom.SYMBOL)
                    .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                    .inV()
                    .values("value")
                    .next();

            if(typeObjs.containsKey(typeName)){
                IRType typeObj2 = typeObjs.get(typeName);
                typeNames.put(v, typeName);
                return typeObj2;
            }

            IRType typeObj = new ExternDataType(g, v, typeType);
            registerTypeObjToVertex(v, typeObj);
            return typeObj;

        }

        private IRType handleBaseType(Vertex v, String typeType){
            List<Object> subterms = 
                g.V(v).outE(Dom.SYN)
                      .order().by(Dom.Syn.E.ORD, Order.asc)
                      .inV()
                      .values("value").toList();

            String term = subterms.stream().map(o -> (String) o).collect(Collectors.joining(""));
            if(typeObjs.containsKey(term)){
                IRType typeObj2 = typeObjs.get(term);
                typeNames.put(v, term);
                return typeObj2;
            }

            IRType typeObj = new BaseType(g,v, typeType);
            registerTypeObjToVertex(v, typeObj);
            return typeObj;
        }

        private void registerTypeObjToVertex(Vertex v, IRType typeObj) {
            typeNames.put(v, typeObj.getName());
            
            if(typeObjs.containsKey(typeObj.getName()))
                return;

            typeObjs.put(typeObj.getName(), typeObj);
        }
    }

}
