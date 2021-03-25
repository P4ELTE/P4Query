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
package p4query.applications.smc.hir.iset;

import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;

public class ProcedureDone implements Instruction {

    private Definition procDef;

    public ProcedureDone(Definition procedureDefinition) {
        this.procDef = procedureDefinition;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        int paramCount = this.procDef.getLocal().getSize();
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Const(new Int(0, procDef.getName() + " terminates with status OK")));
        String comment = procDef.getName() + " has " + paramCount + "parameters";
        insts.add(new Return(new LocalAddress(paramCount + 1, comment), new Size(1, "") ));
        return insts;
    }

    @Override
    public Vertex getOrigin() {
        return null;
    }
    
}
