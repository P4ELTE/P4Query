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
package p4query.applications.smc.hir.externs.implem;

import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Dec;
import p4query.applications.smc.lir.iset.Eq;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.Inc;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Store;
import p4query.applications.smc.lir.iset.Top;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;

public class MemCmpImpl extends ExternDefinition {

    public MemCmpImpl(Declaration iface) throws UnableToLinkDeclaration {
        super(iface, "memcmp", "stdlib");
        //TODO Auto-generated constructor stub
    }

    private Builder instLayout;

    public void init(CompilerState state) {
        this.instLayout = state.getInstLayout();
    }


    // TODO this should go right-to-left, because it quicker because of endianess
    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(super.openDefinition());

        StackInstruction success = new Pop();
        StackInstruction fail = new Pop();
        StackInstruction top = new Top();
        StackInstruction ret = new Return(new LocalAddress(0, ""), new Size(1, "") );

        insts.add(new Load(new LocalAddress(2, "length")));
        insts.add(top);

        insts.add(new IfEq(new UnresolvedInstructionLabel(success, "jump if length is 0"))); // pops

        insts.add(new Load(new LocalAddress(0, "src")));

        // duplicate src on the stack to increment it
        insts.add(new Top()); 
        insts.add(new Inc()); // increase src 
        insts.add(new Store(new LocalAddress(0, "src"))); // pops duplicate

        // read src bit
        insts.add(new GetField());  // also pops src

        // dst
        insts.add(new Load(new LocalAddress(1, "dst")));

        // duplicate dst on the stack to increment it
        insts.add(new Top()); 
        insts.add(new Inc()); // increase src 
        insts.add(new Store(new LocalAddress(1, "dst"))); // pops duplicate

        insts.add(new GetField());  // also pops dst

        // START of difference from memcpy
        // compare src and dst
        insts.add(new Eq()); // also pops src bit and dst
        insts.add(new IfEq(new UnresolvedInstructionLabel(fail, "jump if not equal"))); // pops
        // END of difference from memcpy

        insts.add(new Dec()); // decrease length 
        insts.add(new Goto(new UnresolvedInstructionLabel(top, "loop")));

        // START of difference from memcpy
        insts.add(fail); // pop length
        insts.add(new Const(new Int(0, "fail")));
        insts.add(new Goto(new UnresolvedInstructionLabel(ret, "return")));

        insts.add(success); // pop length
        insts.add(new Const(new Int(1, "success")));
        // END of difference from memcpy

        insts.add(ret);
        insts.add(super.closeDefinition());
        
        instLayout.registerProc(getDeclaration(), insts.getFirst(), ret);

        return insts;
    }
    
}
