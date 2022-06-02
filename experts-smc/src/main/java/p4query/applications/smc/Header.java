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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.DerefTop;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.PopN;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Sub;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;

public class Header {
    private LinkedHashMap<String, Integer> schema;
    private LinkedHashMap<String, int[]> data;
    private String name;

    public String getName(){return name;};

    public Header(String name, LinkedHashMap<String, Integer> schema, LinkedHashMap<String, int[]> data){
        this.schema = schema;
        this.data = data;
        this.name = name;
    }

    private LinkedHashMap<String, Integer> getSchema() {
        return schema;
    }
    private LinkedHashMap<String, int[]> getData() {
        return data;
    }


    public int getSize(){
        return getSchema().values().stream().mapToInt(i -> i).sum();
    }

    public static int[] bigIntegerToBitArray(BigInteger val, int expectedSize){
        String s = val.toString(2);
        // System.out.println("field:" + val + " -> " + s);
        int actualSize = s.length();
        if (actualSize > expectedSize)
            throw new NumberFormatException(
                    String.format("BigInteger %s does not fit expected size %s", val, expectedSize));
        if (actualSize < expectedSize) {
            // padding
            s = String.format("%" + expectedSize + "s", s).replace(' ', '0');
        }

        int[] fieldVal = s.chars().map(i -> i - '0').toArray();
        return fieldVal;

    }

    public Header set(String field, BigInteger val){
        if(!getSchema().containsKey(field)){
            throw new RuntimeException(
                String.format(
                    "Header %s has no field %s in schema %s.",
                        getName(),
                        getSchema(),
                        field));
        }
        int expectedSize = getSchema().get(field);
        int[] fieldVal;
        try{
            fieldVal = bigIntegerToBitArray(val, expectedSize);
        } catch(NumberFormatException e){
            throw new RuntimeException(
                String.format("Header %s field %s has the wrong length. Schema: %s.",
                              getName(), field, getSchema()), e); 
        }

        getData().put(field, fieldVal);

        return this;
    }

    @Override
    public String toString() {
        LinkedHashMap<String, String> data2 = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> e : getData().entrySet()) {
            String v = Arrays.stream(e.getValue())
                             .mapToObj(i -> Integer.toString(i, 10))
                             .collect(Collectors.joining());
            data2.put(e.getKey(), v);
        }
        return String.format("Header [name=%s, schema=%s, data=%s]", getName(), getSchema(), data2);
    }

    public List<StackInstruction> compileToLIR(int packetInAddr, int packetInSize){
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("start of header " + getName()));

        int currentAddr = packetInAddr;
        for(String field : getSchema().keySet()){
            int size = getSchema().get(field);
            String fullFieldName = getName() + "." + field;
            insts.add(new Comment("start of field " + fullFieldName + " (" + size + ")"));

            for (int i = 0; i < getSchema().get(field); i++) {
                insts.add(new Const(new Int(getData().get(field)[i], i + "th bit")));
            }

            // first_bit = top - size
            insts.add(new DerefTop());
            insts.add(new Const(new Size(size - 1, fullFieldName + " - 1")));
            insts.add(new Sub()); 

            insts.add(new Const(new GlobalAddress(currentAddr, "target addr of field " + fullFieldName + " in packet_in")));
            insts.add(new Const(new Size(size, fullFieldName)));
            insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcpy", ""), new Size(3, "src, dst, length")));
            insts.add(new Pop());

            insts.add(new PopN(new Size(size, fullFieldName)));

            insts.add(new Comment("end of field " + fullFieldName + " (" + size + ")"));

            currentAddr = currentAddr + size;

        }
        insts.add(new Comment("end of header " + getName()));
        return insts;
    }


    public static class Ethernet extends Header {
        private static LinkedHashMap<String, Integer> schema = new LinkedHashMap<String, Integer>(); 
        static {
            schema.put("src", 48);
            schema.put("dst", 48);
            schema.put("etherType", 16);
        }

        public Ethernet(){
            super("ethernet", initSchema(), initData());
        }
        static private LinkedHashMap<String, Integer> initSchema(){
            return new LinkedHashMap<String, Integer>(Ethernet.schema);
        }
        static private LinkedHashMap<String, int[]> initData(){
            LinkedHashMap<String, int[]> data = new LinkedHashMap<String, int[]>();
            for ( Map.Entry<String, Integer> e : Ethernet.schema.entrySet()) {
               data.put(e.getKey(), new int[e.getValue()]);
            }
            return data;
        }

    }

    public static class IPv4 extends Header {
        private static LinkedHashMap<String, Integer> schema = new LinkedHashMap<String, Integer>(); 
        static {
            schema.put("version"       , 4);
            schema.put("ihl"           , 4);
            schema.put("diffserv"      , 8);
            schema.put("totalLen"      , 16);
            schema.put("identification", 16);
            schema.put("flags"         , 3);
            schema.put("fragOffset"    , 13);
            schema.put("ttl"           , 8);
            schema.put("protocol"      , 8);
            schema.put("hdrChecksum"   , 16);
            schema.put("srcAddr"       , 32);
            schema.put("dstAddr"       , 32);
        }

        public IPv4(){
            super("ipv4", initSchema(), initData());
        }
        static private LinkedHashMap<String, Integer> initSchema(){
            return new LinkedHashMap<String, Integer>(IPv4.schema);
        }
        static private LinkedHashMap<String, int[]> initData(){
            LinkedHashMap<String, int[]> data = new LinkedHashMap<String, int[]>();
            for ( Map.Entry<String, Integer> e : IPv4.schema.entrySet()) {
               data.put(e.getKey(), new int[e.getValue()]);
            }
            return data;
        }
    }

    public static class Series extends Header {

        public Series(int start, int size){
            super( "series", initSchema(start, size), initData(start, size));
        }

        static private LinkedHashMap<String, Integer> initSchema(int start, int size){
            LinkedHashMap<String, Integer>schema = new LinkedHashMap<String, Integer>(); 
            schema.put("data", size);
            return schema;
        }
        static private LinkedHashMap<String, int[]> initData(int start, int size){
            LinkedHashMap<String, int[]> data = new LinkedHashMap<String, int[]>();
            int[] values = IntStream.iterate(start, x -> x + 1).limit(size).toArray();
            data.put("data", values);
            return data;
        }

    }


    public static class Constant extends Header {

        public Constant(int value, int size){
            super( "const", initSchema(size), initData(value, size));
        }
        static private LinkedHashMap<String, Integer> initSchema(int size){
            LinkedHashMap<String, Integer>schema = new LinkedHashMap<String, Integer>(); 
            schema.put("data", size);
            return schema;
        }
        static private LinkedHashMap<String, int[]> initData(int value, int size){
            LinkedHashMap<String, int[]> data = new LinkedHashMap<String, int[]>();
            int[] values = IntStream.generate(() -> value).limit(size).toArray();
            data.put("data", values);
            return data;
        }
    }
}
