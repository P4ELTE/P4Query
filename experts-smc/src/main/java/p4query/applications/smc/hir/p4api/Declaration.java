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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.ontology.Dom;

public abstract class Declaration {

    private Definition definition;

    public Definition getDefinition(){
        return this.definition;
    };

//    public List<Instruction> getBody() ;
    abstract public String getNamespace()  ;
    abstract public String getName(); 
    abstract public LocalStruct getParameters() ;   
    abstract public LocalStruct getTemps() ;   
    abstract public LocalStruct getControlLocals() ;   
    abstract public LocalStruct getLocals() ;   
	abstract public String addTemporary(IRType type);

    // In P4, actions can also scope the parameters of the parent control, which is
    // weird.
    // To achieve lexical scoping, I duplicate the parameters of the parent in the
    // action as well.
    public static LinkedHashMap<String, IRType>
 stealParamsFromParentControl(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory) {

        List<Vertex> ctl = 
            g.V(src).repeat(__.inE(Dom.SYN).outV())
                    .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))
                    .toList();

        if (ctl.isEmpty()) {
            return new LinkedHashMap<>();
        }

        ProcedureDeclaration pdef = new ProcedureDeclaration(g, ctl.get(0), new IRType.SingletonFactory(typeFactory));

        // local.prependFields(pdef.local.getFields());
        return pdef.getParameters().getFields();

    }

    @Override
    public String toString() {
//        return String.format("%s::%s/%s", getNamespace(), getName(), getLocal().getFields().size());
        return String.format(getClass().getSimpleName() + "[ns=%s, name=%s, arity=%s, fields=%s]", getNamespace(), getName(), getParameters().getFields().size(),  getParameters().getFields().keySet());
    }
}
