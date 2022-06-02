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

import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.InstructionList;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.iset.Instruction;
import p4query.applications.smc.hir.iset.ProcedureDone;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

// TODO namespace
public class ProcedureDefinition extends Definition {

    private InstructionList body;
    private Vertex src;
    private InstructionLayout.Builder instLayout;

    public ProcedureDefinition(ProcedureDeclaration iface) {
        this.src = iface.getSrc();
    }


    // this is meant to be called after all procedure definition signatures were processed
    @Override
    public void init(CompilerState state){
        this.instLayout = state.getInstLayout();

        state.setParentDecl(this.getDeclaration());
        this.body = new InstructionList(state, src);
        state.setParentDecl(null);
    }

    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {
        LocalMemoryLayout local = 
            new LocalMemoryLayout(getDeclaration().getParameters()); 

        List<StackInstruction> insts = new LinkedList<>();

        insts.add(super.openDefinition(getDeclaration().getParameters().commaSeparatedFieldList()));

        StackInstruction exit = null;
        for (Instruction inst : this.body.getList()) {
            List<StackInstruction> res = inst.compileToLIR(local, global);
            instLayout.registerAll(inst.getOrigin(), res);
            if(res == null)
                insts.add(null);
            else
                insts.addAll(res);

            if(inst instanceof ProcedureDone){
                exit = res.get(0);
            }
        }

        instLayout.registerProc(getDeclaration(), insts.get(0), exit);

        insts.add(super.closeDefinition(getDeclaration().getParameters().commaSeparatedFieldList()));
        return insts;
    }

}
