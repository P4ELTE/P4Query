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
package p4query.applications.smc.hir;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.iset.Instruction;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

// TODO namespace
public class ProcedureDefinition implements Definition {
    private String name;
    private String namespace;
    private String origClass;
    // private final LinkedHashMap<String, IRType> locals = new LinkedHashMap<>();
    // // stored in input order
    private final LocalStruct local = new LocalStruct(this);

    private InstructionList body;
    private GraphTraversalSource g;
    private Vertex src;
    private IRType.SingletonFactory typeFactory;
    private InstructionLayout.Builder instLayout;

    ProcedureDefinition(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory) {
        this.g = g;
        this.src = src;
        this.typeFactory = typeFactory;
        this.origClass = (String) g.V(src).values("class").next();
        this.name = (String) g.V(src).outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                .values("value").next();

        fillParameters();
    }

    public ProcedureDefinition(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory, InstructionLayout.Builder instLayout) {
        this(g, src, typeFactory);
        this.instLayout = instLayout;
        this.body = new InstructionList(g, src, typeFactory, this, instLayout);

    }

    private void fillParameters() {

        if (g.V(src).has(Dom.Syn.V.CLASS, "ActionDeclarationContext").hasNext()) {
            LinkedHashMap<String, IRType> parentPars = Definition.stealParamsFromParentControl(g, src, typeFactory);
            for (Map.Entry<String, IRType> m : parentPars.entrySet()) {
                local.appendField(m.getKey(), m.getValue());
            }
        }

        List<Map<String, Object>> nameAndType = g.V(src)
                .optional(__.outE(Dom.SYN)
                        .or(__.has(Dom.Syn.E.RULE, "parserTypeDeclaration"),
                                __.has(Dom.Syn.E.RULE, "controlTypeDeclaration"))
                        .inV())
                .outE(Dom.SITES).has(Dom.Sites.ROLE, Dom.Sites.Role.HAS_PARAMETER).inV()
                .project("name", "type")
                .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                      .inV().values("value"))
                .by(__.outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                      .inV().inE(Dom.SYMBOL)
                      .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV())
                .toList();

        Collections.reverse(nameAndType);

        for (Map<String, Object> map : nameAndType) {
            String name = (String) map.get("name");
            Vertex type = (Vertex) map.get("type");

            local.appendField(name, typeFactory.create(type));
        }
    }

    @Override
    public String toString() {
        return "FunctionDefinition [name=" + name + ", type=" + origClass + ", local=" + local + ", body=" + body + "]";
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
        return local.addTemporary(type);
    }

    public LocalMemoryLayout getMemoryLayout() {
        return new LocalMemoryLayout(local.getFields());
    }


    @Override
    public LocalStruct getLocal() {
        return local;
    }

    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {

        LocalMemoryLayout local = getMemoryLayout(); 
        List<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("define " + name + "(" + getLocal().commaSeparatedFieldList() + ")"));
        for (Instruction inst : this.body.getList()) {
            List<StackInstruction> res = inst.compileToLIR(local, global);
            instLayout.registerAll(inst.getOrigin(), res);
            if(res == null)
                insts.add(null);
            else
                insts.addAll(res);
        }
        insts.add(new Comment(" "));

        instLayout.registerProc(this, insts.get(0));

        return insts;
    }

    

}
