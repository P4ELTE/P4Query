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
import java.io.File;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.math.Fraction;

import p4query.applications.smc.hir.externs.V1ModelUseCase;

public class UseCaseFromJSON extends V1ModelUseCase  {
    private UseCaseFromJSON(IPacket pdist, Map<String, TableEntry[]> tableContents) {

       super(pdist, tableContents);
       
    }

    public static class Factory {
        private static class TableJSON {
            public String tableName;
            public List<TableEntryJSON> entries;
        }

        private static class TableEntryJSON {
            public String actionName;
            public List<BigInteger> patterns;
        }

        private static class SchemaEntryJSON {
            public String fieldName;
            public Integer size;
        }

        private static class DataEntryJSON {
            public String fieldName;
            public BigInteger value;
        }

        private static class HeaderJSON {
            public String headerName;
            public List<SchemaEntryJSON> schema;
            public List<DataEntryJSON> data;
        }

        private static class PacketDistEntryJSON {
            public String prob;
            public List<HeaderJSON> packet;

        }

        private static class RootJSON {
            public List<TableJSON> tableContents;
            public List<PacketDistEntryJSON> packetDistribution;
        }

        public static UseCaseFromJSON create(String pathToJSON){
            ObjectMapper mapper = new ObjectMapper();
            RootJSON root;
            try {
                root = mapper.readValue(new File(pathToJSON), RootJSON.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            IPacket packetDistrib = buildPacketDistrib(root);
            Map<String, TableEntry[]> tableContents = buildTableContents(root);

            return new UseCaseFromJSON(packetDistrib, tableContents);

        }

        private static Map<String, TableEntry[]> buildTableContents(RootJSON root) {

            Map<String, TableEntry[]> tables = new LinkedHashMap<>();
            for (TableJSON tab : root.tableContents) {
              TableEntry[] ents = 
                tab.entries
                   .stream()
                   .map(tjson -> new TableEntry(tjson.actionName,
                                                tjson.patterns.stream().toArray(BigInteger[]::new)))
                   .toArray(TableEntry[]::new);

                tables.put(tab.tableName, ents);
            }
            
            return tables;
        }

        private static IPacket buildPacketDistrib(RootJSON root) {
            PacketDistrib.Builder pdbuilder = new PacketDistrib.Builder();
            for (PacketDistEntryJSON pdent : root.packetDistribution) {
                                
                pdbuilder.addPacket(Fraction.getFraction(pdent.prob), buildPacket(pdent.packet));
            }
            return pdbuilder.build();
        }

        private static Packet buildPacket(List<HeaderJSON> packet) {
            Packet p = new Packet();
            for (HeaderJSON hdr : packet) {

                LinkedHashMap<String, Integer> schema = new LinkedHashMap<>();
                LinkedHashMap<String, int[]> data = new LinkedHashMap<>();

                if(hdr.schema == null){
                    p.put(autoBuildHeader(hdr.headerName, hdr.data));
                    continue;
                }

                for (SchemaEntryJSON schent : hdr.schema) {
                    schema.put(schent.fieldName, schent.size);
                }

                System.out.println(schema);

                if(hdr.data == null){
                    new RuntimeException(
                            String.format("Use-case JSON: data field %s of header %s has user-defined schema, but no data field. ", hdr.headerName));
                }

                for (DataEntryJSON dataent : hdr.data) {
                    Integer size = schema.get(dataent.fieldName);
                    if(size == null){
                       throw
                           new RuntimeException(
                                 String.format("Use-case JSON: data field %s of header %s has no matching field in schema %s ", dataent.fieldName, hdr.headerName, hdr.schema));
                    }

                    data.put(dataent.fieldName, Header.bigIntegerToBitArray(dataent.value, size));
                }

                p.put(new Header(hdr.headerName, schema, data));
            }
            return p;
        }

        private static Header autoBuildHeader(String headerName, List<DataEntryJSON> data) {
            Header hdr;
            if(headerName.equals("ethernet")){
                hdr = new Header.Ethernet();
            } else if(headerName.equals("ipv4")){
                hdr = new Header.IPv4();
            } else {
                throw new RuntimeException("Use-case JSON: header " + headerName + " is missing schema field. Only the following headers have built-in schemas: ethernet, ipv4");
            }

            if(data == null)
                return hdr;

            for (DataEntryJSON dataent : data) {
                hdr.set(dataent.fieldName, dataent.value);
            }

            return hdr;
        }

        private final static String example = 

  " {                                                               "
+ " \"tableContents\" :                                             "
+ "     [ { \"tableName\" : \"table name\",                         "
+ "         \"entries\" : [{ \"actionName\" : \"action name\",      "
+ "                          \"patterns\" : [0, 1] }] } ] ,         "
+ " \"packetDistribution\" :                                        "
+ "    [ { \"prob\" : \"1/2\" ,                                     "
+ "        \"packet\" :                                             "
+ "            [ { \"schema\" : [ { \"fieldName\" : \"field name\", "
+ "                               \"size\" : 15 } ] ,               "
+ "                \"headerName\"   : \"header name\",                    "
+ "                \"data\"   : [ { \"fieldName\" : \"field name\", "
+ "                               \"value\" : 1 } ] } ] } ]         "
+ " }                                                               ";


    }
}
