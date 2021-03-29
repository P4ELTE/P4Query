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
package p4query.applications.smc.hir.externs;

import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;

public class PacketInExtract extends ExternDefinition {

    private Builder instLayout;

    public PacketInExtract(InstructionLayout.Builder instLayout) {
        super("extract", "packet_in");
        this.instLayout = instLayout;

//        for (Object obj : names) {
//            body.add(new Invoke(new UnresolvedNameLabel("", (String) obj, ""), new Size(0, "")));
//        }
    }

    // // packet.buffer
    // load 0 // packet
    // const 1  // offset: cursor 
    // add

    // // header contents
    // load 1 // header
    // const 2 // offset: validity bit, size
    // add

    // // header size
    // load 1
    // const 1 // offset: validity bit
    // add

    // invoke memcpy 
    // pop


    // // packet.cursor += 1
    // load 0 // packet.cursor
    // getfield
    // const 1
    // add
    // load 0 // packet.cursor
    // putfield

    // const 0
    // return

    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("define " + name + "()"));

        insts.add(new Comment("memcpy(hdr, packet.buffer, hdr.size)"));
        insts.add(new Comment("packet.buffer"));
        insts.add(new Load(new LocalAddress(0, "packet")));
        insts.add(new Const(new Size(1, "offset: cursor")));
        insts.add(new Add());

        insts.add(new Comment("contents of hdr"));
        insts.add(new Load(new LocalAddress(1, "hdr")));
        insts.add(new Const(new Size(2, "offset: validity bit, size")));
        insts.add(new Add());

        insts.add(new Comment("hdr.size"));
        insts.add(new Load(new LocalAddress(1, "hdr")));
        insts.add(new Const(new Size(1, "offset: validity bit")));
        insts.add(new Add());

        insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcpy", ""), new Size(3, "src, dst, length")));
        insts.add(new Pop());

        insts.add(new Comment("packet.cursor += 1"));
        insts.add(new Load(new LocalAddress(0, "packet.cursor")));
        insts.add(new GetField());
        insts.add(new Const(new Size(1, "")));
        insts.add(new Add());
        insts.add(new Load(new LocalAddress(0, "packet.cursor")));
        insts.add(new PutField());

        insts.add(new Const(new Int(0, getName() + " terminates with status OK")));
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));
        insts.add(new Comment(" "));
        
        instLayout.registerProc(this, insts.getFirst());

        return insts;
    }

}
