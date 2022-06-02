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
package p4query.applications.smc.hir.p4api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.ontology.Dom;

public class TableDeclaration extends Declaration {
    private String name;
    private String namespace;
    private String origClass;
    // private final LinkedHashMap<String, IRType> locals = new LinkedHashMap<>();
    // // stored in input order
    private final LocalStruct local = new LocalStruct(this);
    private final LocalStruct temps = new LocalStruct(this);
    private final LocalStruct controlLocals = new LocalStruct(this); // this will stay empty
    private LocalStruct locals = new LocalStruct(this); // this will stay empty

    private Vertex src;

    private CompilerState state;


    public List<Expression> keys = new LinkedList<>();
    private List<String> actionNames;

    public TableDeclaration(CompilerState state, Vertex src) {
        this.state = state;
        this.src = src;
        this.origClass = (String) state.getG().V(src).values(Dom.Syn.V.CLASS).next();

        if(!origClass.equals("TableDeclarationContext")) 
            throw new IllegalArgumentException("TableDeclarationContext node expected");

        this.name = (String) state.getG().V(src).outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                .values("value").next();

        this.namespace = findNamespace();

        fillParameters();

        locals.appendField("hit", new GenType("hit", 1, null));

        fillKeys(state);
        fillActionNames(state);
    }

    private void fillActionNames(CompilerState state) {
        GraphTraversalSource g = state.getG();
        List<Object> actionNames0 = 
            g.V(getSrc()).outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList")
                    .inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TablePropertyContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "actionList").inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "ActionRefContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name")
                    .inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .values("value")
                    .toList();
        actionNames = actionNames0.stream().map(o -> (String) o).collect(Collectors.toList());
        Collections.reverse(actionNames);
    }

    private void fillKeys(CompilerState state) {
        GraphTraversalSource g = state.getG();

        //        for (Vertex k : keyVerts) {
        //            // NOTE: not sure if the spec allows for literals here, but we don't
        //            state.setParentDecl(this.getDeclaration());
        //            keys.add(Expression.Factory.createLvalue(state, k));
        //            state.setParentDecl(null);
        //        }

        List<Vertex> keyVerts = 
            g.V(getSrc()).outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList")
                    .inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TablePropertyContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "keyElementList").inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression")
                    .inV()
                    .toList();

        for (Vertex k : keyVerts) {
            // NOTE: not sure if the spec allows for literals here, but we don't
            state.setParentDecl(this);
            keys.add(Expression.Factory.createLvalue(state, k));
            state.setParentDecl(null);
        }
    }

    private String findNamespace() {

        
        List<Object> maybeNs = 
            state.getG().V(src)
            .repeat(__.inE(Dom.SYN).outV())
            .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
            .values("value")
            .toList();
        
        // note: global extern methods are also enclosed in an extern declaration, but this declaration has no name
        if(maybeNs.isEmpty())
            return null;

        String control = (String) maybeNs.get(0);

        return control;
    }

    private void fillParameters() {

        GraphTraversalSource g = state.getG();

        LinkedHashMap<String, IRType> parentPars = 
            Declaration.stealParamsFromParentControl(state.getG(), src, state.getTypeFactory());
        for (Map.Entry<String, IRType> m : parentPars.entrySet()) {
            local.appendField(m.getKey(), m.getValue());
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public Vertex getSrc() {
        return src;
    }

    public String addTemporary(IRType type) {
//        return local.addTemporary(type);
        return temps.addTemporary(type, type.getName());
    }

    @Override
    public LocalStruct getParameters() {
        return local;
    }

    @Override
    public LocalStruct getTemps() {
        return temps;
    }
    @Override
    public LocalStruct getControlLocals() {
        return this.controlLocals;
    }

    @Override
    public LocalStruct getLocals() {
        return this.locals;
    }

    public List<Expression> getKeys() {
        return this.keys;
    }

    public List<String> getActionNames() {
        return this.actionNames;
    }

}
