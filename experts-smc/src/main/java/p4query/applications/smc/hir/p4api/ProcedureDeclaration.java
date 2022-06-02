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
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.InstructionList;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.ontology.Dom;

// TODO namespace
public class ProcedureDeclaration extends Declaration {
    private String name;
    private String namespace;
    private String origClass;
    // private final LinkedHashMap<String, IRType> locals = new LinkedHashMap<>();
    // // stored in input order
    private final LocalStruct parameters = new LocalStruct(this);
    private final LocalStruct temps = new LocalStruct(this);
    private LocalStruct controlLocals = new LocalStruct(this);
    private LocalStruct locals = new LocalStruct(this);

    private InstructionList body;
    private GraphTraversalSource g;
    private Vertex src;
    private IRType.SingletonFactory typeFactory;
    private InstructionLayout.Builder instLayout;

    public ProcedureDeclaration(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory) {
        this.g = g;
        this.src = src;
        this.typeFactory = typeFactory;
        this.origClass = (String) g.V(src).values("class").next();
        this.name = (String) g.V(src).outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                .values("value").next();

        this.namespace = findNamespace();

        fillParameters();


        fillLocalDeclarations();
        fillControlLocalInstantiations();

    }

    private String findNamespace() {
        if(g.V(src).has(Dom.Syn.V.CLASS, "ActionDeclarationContext").hasNext()){
            return findActionNamespace();
        } else if(g.V(src).has(Dom.Syn.V.CLASS, "FunctionPrototypeContext").hasNext()){ 
            return findExternNamespace();
        } else {
            return null;
        }
    }

    private String findActionNamespace() {
        List<Object> maybeNs = 
            g.V(src)
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

    public String findExternNamespace(){
        List<Object> maybeNs = 
            g.V(src)
            .repeat(__.inE(Dom.SYN).outV())
            .until(__.has(Dom.Syn.V.CLASS, "ExternDeclarationContext"))
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
            .values("value")
            .toList();
        
        // note: global extern methods are also enclosed in an extern declaration, but this declaration has no name
        if(maybeNs.isEmpty())
            return null;

        String extern = (String) maybeNs.get(0);

        return extern;
    }

    private void fillParameters() {

        if (g.V(src).has(Dom.Syn.V.CLASS, "ActionDeclarationContext").hasNext()) {
            LinkedHashMap<String, IRType> parentPars = Declaration.stealParamsFromParentControl(g, src, typeFactory);
            for (Map.Entry<String, IRType> m : parentPars.entrySet()) {
                parameters.appendField(m.getKey(), m.getValue());
            }
        }

        List<Map<String, Object>> nameAndType;

        List<Vertex> params = 
            g.V(src)
             .optional(
                 __.outE(Dom.SYN)
                   .or(__.has(Dom.Syn.E.RULE, "parserTypeDeclaration"),
                __.has(Dom.Syn.E.RULE, "controlTypeDeclaration"))
             .inV())
             .outE(Dom.SITES).has(Dom.Sites.ROLE, Dom.Sites.Role.HAS_PARAMETER).inV()
             .toList();
        Collections.reverse(params);

        for (Vertex par : params) {
            
            String name = 
                (String) g.V(par).outE(Dom.SYMBOL)
                            .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                            .inV().values("value").next();

            Vertex type = 
                g.V(par).outE(Dom.SYMBOL)
                        .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                        .optional( // in case its not a basetype
                            __.inE(Dom.SYMBOL)
                              .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                              .outV())
                        
                        .next();

            // note: generic type parameters don't have a node for declaring type 
            if(g.V(type).has(Dom.Syn.V.CLASS, "TerminalNodeImpl").hasNext()){
                String typePar = (String) g.V(type).values("value").next();
                parameters.appendField(name, typeFactory.create(typePar, 1, null));
            } else {
                parameters.appendField(name, typeFactory.create(type));
            }

        }
    }

    private void fillLocalDeclarations() {
        if(!g.V(src)
             .or(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"), 
                 __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"))
             .hasNext())
            return;

        List<Map<String, Object>> insts = 
            g.V(src)
                .optional(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlBody").inV())
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "blockStatement").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.or(__.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
                            __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"),
                            __.has(Dom.Syn.V.CLASS, "InstantiationContext")))
             
             .project("name", "type")
             .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV().values("value"))
             .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                   .optional( // in case its not a basetype
                       __.inE(Dom.SYMBOL)
                           .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                           .outV()))
             .toList();

        if(insts.isEmpty()) 
            return;

        for (Map<String,Object> map : insts) {
           String name = (String) map.get("name"); 
           Vertex type = (Vertex) map.get("type"); 

           this.locals.appendField(name, typeFactory.create(type));
        }
    }

    // these are declarations outside the control body
    private void fillControlLocalInstantiations() {

        if(!g.V(src).has(Dom.Syn.V.CLASS, "ControlDeclarationContext").hasNext())
            return;

        List<Map<String, Object>> insts = 
            g.V(src)
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlLocalDeclarations").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "ControlLocalDeclarationContext")) 
                .outE(Dom.SYN).inV()
                .or(__.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
                    __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"),
                    __.has(Dom.Syn.V.CLASS, "InstantiationContext"))
             .project("name", "type")
             .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV().values("value"))
             .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                   .optional( // in case its not a basetype
                       __.inE(Dom.SYMBOL)
                           .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                           .outV()))
             .toList();
            
        if(insts.isEmpty()) 
            return;

        for (Map<String,Object> map : insts) {
           String declName = (String) map.get("name"); 
           Vertex declType = (Vertex) map.get("type"); 

           // note: in the current model we assign global life-cycle to control-local instantiations. they look like local variables, but they are initialized at the start of the program.
           // in sequential execution this shouldn't cause problems, since recursion is forbidden.
           // (it's not allowed to "open" an instantiation when it's already opened).            
           // in parallel execution this could cause problems. there, each instantiation lives in its own thread.
           if(g.V(declType).has(Dom.Syn.V.CLASS, "ControlDeclarationContext").hasNext())
               continue;

           this.controlLocals.appendField(declName, typeFactory.create(declType));
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

    public String addTemporary(IRType type) {
//        return local.addTemporary(type);
        return temps.addTemporary(type, type.getName());
    }

    @Override
    public LocalStruct getParameters() {
        return parameters;
    }

    @Override
    public LocalStruct getTemps() {
        return temps;
    }
    @Override
    public LocalStruct getLocals() {
        return locals;
    }

    @Override
    public LocalStruct getControlLocals() {
        return this.controlLocals;
    }


    public Vertex getSrc() {
        return src;
    }
}
