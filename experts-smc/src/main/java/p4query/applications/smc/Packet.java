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
package p4query.applications.smc;

import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.lir.iset.StackInstruction;

public class Packet implements IPacket {
    private LinkedList<Header> headers = new LinkedList<>();

    public Packet put(Header h){
        headers.add(h);

        return this;
    }

    @Override
    public LinkedList<StackInstruction> compileToLIR(int packetInAddr, int packetInSize, StackInstruction junction){
        int currAddr = packetInAddr;
        int currSize = 0;

        LinkedList<StackInstruction> insts = new LinkedList<>();
        for (Header h : headers) {
            insts.addAll(h.compileToLIR(currAddr, packetInSize - currSize));
            currAddr += h.getSize();
            currSize += h.getSize();
        }


        return insts;
    }

    @Override
    public String toString() {
        return "Packet [headers=" + headers + "]";
    }

    @Override
    public int getSize() {
        return headers.stream().mapToInt(h -> h.getSize()).sum();
    }


}
