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
package p4query.applications.smc.hir.exprs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.beust.jcommander.Strings;

import org.apache.tinkerpop.gremlin.driver.ser.binary.GraphBinaryIo;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.ProcedureDeclaration;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;
import p4query.ontology.Dom;

public class P4StorageReference extends StorageReference {
    private Vertex src;
    private LinkedHashMap<String, IRType> fieldTypes = new LinkedHashMap<>();

    private CompilerState state;
    private Declaration parentDecl;
    private Declaration parentControlDecl;

    P4StorageReference(CompilerState state, Vertex src) throws UnableToParseException {
        this(state, src, false);
    }
    
    P4StorageReference(CompilerState state, Vertex src, boolean ignoreCheck) throws UnableToParseException {
        GraphTraversalSource g = state.getG();
        this.src = src;
        this.parentDecl = state.getParentDecl();
        this.parentControlDecl = findParentControlDecl(state);

        if (!ignoreCheck && 
            !g.V(src)
                .or(__.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
                    __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonTypeName"), 
                    __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "prefixedNonTypeName")).hasNext()){
            throw new UnableToParseException(P4StorageReference.class, src);
        }

        this.state = state;

        String typeType = (String) g.V(src).values(Dom.Syn.V.CLASS).next();

        if(!(Arrays.asList("LvalueContext", "ExpressionContext", "VariableDeclarationContext").contains(typeType)))
            throw new IllegalArgumentException(
                String.format("Store reference cannot be initilized on %s vertex %s.", typeType, src));

        fillFields();

    }


    @Override
    public String getFirstFieldName(){
        return getFieldList().get(0);
    }

    @Override
    public String getTailFields(){
        LinkedList<String> tail = new LinkedList<>(getFieldList());
        tail.removeFirst();
        return Strings.join(".", tail);
    }


    @Override
    public String toString() {
        return "StorageReference [fieldTypes=" + fieldTypes + "]";
    }

    // how much we need to go from the source address to reach the address of the last field?
    @Override
    public int getSizeOffset(){
        int currAddr = 0;

        Iterator<String> it = getFieldList().iterator();
        String curr = it.next();
        while(it.hasNext()){
            IRType type = fieldTypes.get(curr);
            if(!(type instanceof Composite)) 
                return currAddr;

            Composite comp = (Composite) type;

            String next = it.next();

            boolean found = false;
            for (Map.Entry<String, IRType> compField : comp.getFields().entrySet()) {
                if(compField.getKey().equals(next)){
                    found = true;
                    break;
                } 

                currAddr += compField.getValue().getSize();
            }
            if(!found)
                throw new IllegalStateException("Field " + next + " not in " + comp);

            curr =  next;
        }

        return currAddr;
    }

    // what is the size of the pointed storage field
    @Override
    public IRType getTypeHint() {
        List<String> fieldList = getFieldList();
        return fieldTypes.get(fieldList.get(fieldList.size() -1));
    }

    private void fillFields() {

        GraphTraversalSource g = state.getG();
        List<Map<String, Object>> fields = 
            g.V(src)
             .optional(__.has(Dom.Syn.V.CLASS, "VariableDeclarationContext")
                         .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV())
             .repeat(__.outE(Dom.SYN)
                       .not(__.has(Dom.Syn.E.RULE, "DOT"))
                       .order().by(Dom.Syn.E.ORD, Order.desc)
                       .inV())
             .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .project("vertex", "name")
             .by(__.identity())
             .by(__.values("value"))
             .toList();

        Collections.reverse(fields);


        for (Map<String,Object> map : fields) {
            Vertex fv = (Vertex) map.get("vertex") ;
            String name = (String) map.get("name") ;

            Vertex typeVert;
            try { 
                typeVert =
                    g.V(fv).inE(Dom.SYMBOL)
                           .or(__.has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES),
                               __.has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME))  // NOTE: in case of variable instantiations we have no scope edge since this is the declaring 
                           .outV()
                           // NOTE: optional will not run in case of table calls (first part of ipv4_lpm.apply())
                           .optional(
                                __
                                .outE(Dom.SYMBOL)
                                .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                                .inV())
                           // NOTE: optional will run in case of structs. it will not run in case of base types.
                           .optional(
                                __
                                .inE(Dom.SYMBOL)
                                .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                                .outV())
                           .next();

            } catch(NoSuchElementException e){
                System.err.println(g.V(fv).elementMap().next());

                throw new IllegalStateException(
                    String.format("No type information found for name %s (vertex %s)", name, fv));
            }

            if(typeVert != null){
                IRType type = state.getTypeFactory().create(typeVert);
                if(fieldTypes.containsKey(name)){
                    throw new IllegalStateException("Field name " + name + " is already stored. Dot expressions with duplicate field names are not supported yet.");
                }
                fieldTypes.put(name, type);
            } else {
                throw new IllegalStateException("Type not found for field " + name + ".");
            }
        }
    }

    @Override
    public String toP4Syntax() {
        return Strings.join(".", getFieldList());
    }

    @Override
    public StorageReference getStorageReference() {
        return this;
    }

    @Override
    protected List<String> getFieldList() {
        return new LinkedList<>(fieldTypes.keySet());
    }

    @Override
    protected LinkedHashMap<String, IRType> getFields() {
        return fieldTypes;
    }

    @Override
    public Declaration getParentDecl() {
        return parentDecl;
    }

    @Override
    public Declaration getParentControlDecl() {
        return parentControlDecl;
    }


}
