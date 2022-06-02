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

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.math.Fraction;

import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.ProbabilisticInstruction;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;

public class PacketDistrib implements IPacket {

    public static class Builder {
        private Map<Packet, Fraction> probDist = new LinkedHashMap<>();
        public Builder addPacket(Fraction probability, Packet p){
            if(probDist.get(p) != null){
               throw new IllegalStateException("That packet was already added to this distribution: " + p);
            }
            probDist.put(p, probability);
            return this;
        }

        public PacketDistrib build(){
           Fraction sum = probDist.values()
                                  .stream()
                                  .reduce(Fraction.ZERO, (a,b) -> a.add(b));
           // NOTE: these reduces have nothing to do with each other.
           //  Stream.reduce is a list fold.
           //  Fraction.reduce simplifies the fraction. 
           sum = sum.reduce();
           if(!sum.equals(Fraction.ONE)){
               throw new IllegalStateException(
                            String.format("Probabilities %s add up to %s instead of 1 in packet distribution %s.", probDist.values(), sum, probDist));
           }
           return new PacketDistrib(probDist);
        }
    }

    private Map<Packet, Fraction> probDist = new LinkedHashMap<>();
    private PacketDistrib(Map<Packet, Fraction> probDist){
        this.probDist = probDist;
    };

    @Override
    public String toString() {
        return "PacketDistrib [probDist=" + probDist + "]";
    }



    // TODO instead of expecting a junction instruction, use a no-op
    @Override
    public LinkedList<StackInstruction> compileToLIR(int packetInAddr, int packetInSize, StackInstruction junction){

        LinkedList<StackInstruction> insts = new LinkedList<>();
        LinkedList<StackInstruction> packets = new LinkedList<>();

        ProbabilisticInstruction.Builder pb = new ProbabilisticInstruction.Builder();

        for (Entry<Packet, Fraction> entry : probDist.entrySet()) {
            LinkedList<StackInstruction> packet = entry.getKey().compileToLIR(packetInAddr, packetInSize, junction);

            packet.add(
                new Goto(
                    new UnresolvedInstructionLabel(
                        junction,
                        "packet received, go to junction")));

            pb.addInstruction(entry.getValue(),
                              new Goto(
                                new UnresolvedInstructionLabel(
                                  packet.getFirst(),
                                  "receive packet " + entry.getKey())));
            packets.addAll(packet);
        }
        
        insts.add(pb.build());
        insts.addAll(packets);

        return insts;
    }

    @Override
    public int getSize() {
        return 
            probDist.keySet()
                    .stream()
                    .max(Comparator.comparing(p -> p.getSize()))
                    .orElseThrow(() -> new RuntimeException("Cannot find maximal size packet in distribution, maybe its empty."))
                    .getSize() ;
    }
}

