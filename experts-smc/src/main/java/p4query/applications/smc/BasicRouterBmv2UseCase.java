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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.Fraction;

import p4query.applications.smc.hir.externs.V1ModelUseCase;

class BasicRouterBmv2UseCase extends V1ModelUseCase  {

        public BasicRouterBmv2UseCase() {
            super(getPacket(), getTableContents());
        }

        // TODO namespaces
        private static Map<String, TableEntry[]> getTableContents() {
            Map<String, TableEntry[]> contents = new HashMap<>();
            // egress
            contents.put("rewrite_mac", new TableEntry[] {}); 

            BigInteger zero = BigInteger.ZERO;
            BigInteger one = BigInteger.ONE;
            
            // ingress
            contents.put("ipv4_fib", new TableEntry[] { new TableEntry("fib_hit_nexthop", zero, zero) });
            contents.put("bd", new TableEntry[] { new TableEntry("set_vrf", one) });
            contents.put("nexthop", new TableEntry[] { new TableEntry("set_egress_details", one) });
            contents.put("port_mapping", new TableEntry[] { new TableEntry("set_bd", one) });

            contents.put("ipv4_fib_lpm", new TableEntry[] { new TableEntry("fib_hit_nexthop", one, one) });

            return contents;
        }

        private static IPacket getPacket() {

//            BigInteger sixteenOnes = new BigInteger("2").pow(16).subtract(BigInteger.ONE);

            IPacket packet = all3();
            System.out.println("packet=" + packet);
            return packet;
        }

        private static Packet series(){
           return new Packet().put(new Header.Series(0,112)) .put(new Header.Series(112,160)); 
        }

        private static Packet minimalPath() {
            // { "name" : "minimal_path" ,
            // "packet" : "Ether(dst='00:00:00:00:00:00')",
            // }
            return new Packet().put(new Header.Ethernet());
        }
        
        private static Packet middlePath() {
            // { "name" : "middle_path" ,
            // "packet" : "Ether(dst='00:00:00:00:00:00')/IP(dst='0.0.0.0')",
            // }

            return
                new Packet()
                    .put(new Header.Ethernet().set("etherType", new BigInteger("2048")))
                    .put(new Header.IPv4());
        }
        
        
        private static Packet maximalPath() {
            // { "name" : "maximal_path" ,
            // "packet" : "Ether(dst='00:00:00:00:00:00')/IP(dst='255.255.255.255')",
            // }
            return
                new Packet()
                    .put(new Header.Ethernet().set("etherType", new BigInteger("2048")))
                    .put(new Header.IPv4().set("dstAddr", new BigInteger("4294967295")));

            //            Packet packet = new Packet().put(new Header.Ethernet().set("etherType", new BigInteger("2048"))).put(new Header.Series(112, 160));
        }

        private static IPacket all3() {
        //      { "name" : "all_3" , 
        //        "packet" : 
        //          { "Ether(dst='00:00:00:00:00:00')" : 0.33,
        //            "Ether(dst='00:00:00:00:00:00')/IP(dst='0.0.0.0')" : 0.33,
        //            "Ether(dst='00:00:00:00:00:00')/IP(dst='255.255.255.255')" : 0.34
        //          }, 
        //      } 
            return
              new PacketDistrib.Builder()
                    .addPacket(Fraction.getReducedFraction(165, 500),
                               new Packet().put(new Header.Ethernet()) )
                    .addPacket(Fraction.getReducedFraction(165, 500),
                               new Packet()
                                     .put(new Header.Ethernet().set("etherType", new BigInteger("2048")))
                                     .put(new Header.IPv4()) )
                    .addPacket(Fraction.getReducedFraction(170, 500),
                               new Packet()
                                       .put(new Header.Ethernet().set("etherType", new BigInteger("2048")))
                                       .put(new Header.IPv4().set("dstAddr", new BigInteger("4294967295"))) )
                    .build();
        }

        private static IPacket midAndMax() {
        //      { "name" : "mid_and_max" , 
        //        "packet" : 
        //          { "Ether(dst='00:00:00:00:00:00')/IP(dst='0.0.0.0')" : 0.5,
        //            "Ether(dst='00:00:00:00:00:00')/IP(dst='255.255.255.255')" : 0.5
        //          }, 
        //      }
            return
              new PacketDistrib.Builder()
                    .addPacket(Fraction.getReducedFraction(1, 2),
                               new Packet()
                                     .put(new Header.Ethernet().set("etherType", new BigInteger("2048")))
                                     .put(new Header.IPv4()) )
                    .addPacket(Fraction.getReducedFraction(1, 2),
                               new Packet()
                                       .put(new Header.Ethernet().set("etherType", new BigInteger("2048")))
                                       .put(new Header.IPv4().set("dstAddr", new BigInteger("4294967295"))) )
                    .build();
        }


    }
