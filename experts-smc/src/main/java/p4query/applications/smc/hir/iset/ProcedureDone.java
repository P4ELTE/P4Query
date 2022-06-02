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
package p4query.applications.smc.hir.iset;

import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;

public class ProcedureDone implements Instruction {

    private Declaration procDef;

    public ProcedureDone(CompilerState state) {
        this.procDef = state.getParentDecl();
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        int paramCount = this.procDef.getParameters().getSize();
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
