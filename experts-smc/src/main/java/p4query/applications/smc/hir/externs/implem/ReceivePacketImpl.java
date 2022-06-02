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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.math.Fraction;

import p4query.applications.smc.IPacket;
import p4query.applications.smc.Packet;
import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.Segment;
import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.externs.ExternDeclaration;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Dec;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.Inc;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.ProbabilisticInstruction;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Store;
import p4query.applications.smc.lir.iset.Top;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;


public class ReceivePacketImpl extends ExternDefinition {

    private final IPacket packet;

    public ReceivePacketImpl(final Declaration iface, final IPacket packet) throws UnableToLinkDeclaration {
        super(iface, "receive_packet", "stdlib");
        this.packet = packet;
    }

    private Builder instLayout;

    public void init(final CompilerState state) {
        this.instLayout = state.getInstLayout();
    }

    @Override
    public List<StackInstruction> compileToLIR(final GlobalMemoryLayout global) {

        int packetInAddr = global.lookupSegmentByName("packet_in").getAddress() + 1;
        int packetInSize = global.lookupSegmentByName("packet_in").getType().getSize() - 1;

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(super.openDefinition());
        StackInstruction exit0 = new Const(new Int(0, getDeclaration().getName() + " terminates with status OK"));
        insts.addAll(packet.compileToLIR(packetInAddr, packetInSize, exit0));
        insts.add(exit0);
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "")));
        insts.add(new Comment(" "));
        insts.add(super.closeDefinition());

        instLayout.registerProc(getDeclaration(), insts.getFirst(), exit0);
        return insts;
    }

}
