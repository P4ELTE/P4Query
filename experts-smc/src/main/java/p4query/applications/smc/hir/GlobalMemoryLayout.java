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
package p4query.applications.smc.hir;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.hir.typing.Struct;

// NOTE in P4 structs declaration also initializes, so struct types and struct names are the same
// NOTE I store global constanst in a struct-like "global", and there is only one
public class GlobalMemoryLayout  {

    private final List<Segment> layout = new LinkedList<>();
    private final LinkedHashMap<String, Segment> index = new LinkedHashMap<>();

	public GlobalMemoryLayout(IRType global) {
        this(Arrays.asList(global));
    }

	public GlobalMemoryLayout(final List<IRType> globals) {

        int currAddr = 0;
        for (IRType type : globals) {
            createLayout(type, currAddr, "", type.getName());
            currAddr += type.getSize();
        }
    }

    public int getSize(){
        Segment lastSeg = layout.get(layout.size() -1 );
        return lastSeg.getAddress() + lastSeg.getType().getSize();
    }

    @Override
    public String toString() {
        return "GlobalMemoryLayout [index={" + toHumanReadable() + "}]";
    }

    public String toHumanReadable() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (Map.Entry<String, Segment> e : index.entrySet()) {
           sb.append("  ");
           sb.append(e.getKey()) ;
           sb.append("=");
           sb.append(e.getValue().getAddress()); 
           sb.append(" (");
           sb.append(e.getValue().getType().getSize()); 
           sb.append(")");
           sb.append("," + System.lineSeparator());
        }
        return sb.toString();
    }
    
    // NOTE: this is recursive, but structs are expected to be shallow 
    private void createLayout(IRType type, int address, String prefix, String fieldName){

//        System.out.println(address + ": " + prefix + " " + fieldName);
        Segment inst = new Segment(type, address, prefix, fieldName, false);
        addToLayout(inst);

        if(type instanceof Composite){
            Composite structType = (Composite) inst.getType();
            int currAddr = address;
            for (Map.Entry<String, IRType> field : structType.getFields().entrySet()) {
                IRType subFieldType = field.getValue();
                String subFieldName = field.getKey();
                String prefix2 = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;

                createLayout(subFieldType, currAddr, prefix2, subFieldName);

//                System.err.println(type);
//                System.err.println(fieldName);
//                System.err.println(subFieldName);

                currAddr += subFieldType.getSize();
            }
        }         
    }

    private void addToLayout(Segment inst) {
        String prefix2 = inst.getPrefix().isEmpty() ? "" : inst.getPrefix() + ".";
        if(index.containsKey(prefix2 + inst.getName()))
            return;
        layout.add(inst);
        index.put(prefix2 + inst.getName(), inst);
    }

    public Segment lookupSegmentByName(String name){
        Segment res = index.get(name);
        if(res != null ) return res;

        // TODO find a nicer solution to play together with GlobalStruct
        return index.get("GLOBAL." + name);
    }

    public List<Segment> getHeaders(){
        List<Segment> hdrs = new LinkedList<>();
        for (Segment segment : layout) {
           if(segment.getType()  instanceof Struct){
             Struct stru = (Struct) segment.getType();
             if(stru.isP4Header()){
                hdrs.add(segment);
             }
           }
        }
        return hdrs;
    }
}
