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
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Inc;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Sub;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;

public class PacketInExtractImpl extends ExternDefinition {

    public PacketInExtractImpl(Declaration iface) throws UnableToLinkDeclaration {
        super(iface, "extract", "packet_in");
        //TODO Auto-generated constructor stub
    }

    private Builder instLayout;

    public void init(CompilerState state) {
        this.instLayout = state.getInstLayout();
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
        insts.add(super.openDefinition());

        // buffer start
        insts.add(new Comment("memcpy(packet.buffer, hdr, hdr.size)"));
        insts.add(new Comment("packet.buffer + packet.cursor"));

        insts.add(new Load(new LocalAddress(0, "packet")));
        insts.add(new Const(new Size(1, "offset: cursor")));
        insts.add(new Add());

        // cursor value
        insts.add(new Load(new LocalAddress(0, "packet")));
        insts.add(new GetField());

        // buffer start + cursor
        insts.add(new Add());

        insts.add(new Comment("contents of hdr"));
        insts.add(new Load(new LocalAddress(1, "hdr"))); // pointer to a header
        insts.add(new GetField()); 
        insts.add(new Const(new Size(2, "offset: validity bit, size")));
        insts.add(new Add());

        insts.add(new Comment("hdr.size"));
        insts.add(new Load(new LocalAddress(1, "hdr"))); // pointer to a header
        insts.add(new GetField()); 
        insts.add(new Const(new Size(1, "offset: validity bit")));
        insts.add(new Add());
        insts.add(new GetField());
//        insts.add(new Const(new Size(2, "decrease copy-size with 2 (validity bit, size are not in packet_in)")));
//        insts.add(new Sub());

        insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcpy", ""), new Size(3, "src, dst, length"))); // pops 
        insts.add(new Pop());

        insts.add(new Comment("set validity to 1"));
        insts.add(new Const(new Int(1, "valid")));
        insts.add(new Load(new LocalAddress(1, "hdr"))); // pointer to a header
        insts.add(new GetField());
        insts.add(new PutField());

        insts.add(new Comment("packet.cursor = packet.cursor + 1"));  
        // packet.cursor + hdr.size
        insts.add(new Load(new LocalAddress(0, "packet.cursor")));
        insts.add(new GetField());

        insts.add(new Load(new LocalAddress(1, "hdr"))); // pointer to a header
        insts.add(new GetField()); 
        insts.add(new Const(new Size(1, "offset: validity bit")));
        insts.add(new Add());
        insts.add(new GetField());
        insts.add(new Add());
//        insts.add(new Const(new Size(2, "decrease increment-size with 2 (validity bit, size are not in packet_in)")));
//        insts.add(new Sub());

        // store value to packet.cursor address
        insts.add(new Load(new LocalAddress(0, "packet.cursor")));
        insts.add(new PutField());

        StackInstruction exit0 = new Const(new Int(0, getDeclaration().getName() + " terminates with status OK"));
        insts.add(exit0);
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));

        insts.add(super.closeDefinition());
        
        instLayout.registerProc(getDeclaration(), insts.getFirst(), exit0);

        return insts;
    }

}
