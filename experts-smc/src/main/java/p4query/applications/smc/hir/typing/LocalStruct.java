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
package p4query.applications.smc.hir.typing;

import java.util.LinkedHashMap;
import java.util.Map;

import p4query.applications.smc.hir.p4api.Declaration;

// TODO this is not a Struct. rename this to LocalComposite
public class LocalStruct implements Composite {

    private LinkedHashMap<String, IRType> fields = new LinkedHashMap<>(); // stored in input order
    private Declaration parentDecl;

    private int tempCounter = 0;

    public LocalStruct(Declaration def) {
        this.parentDecl = def;
    }

    private LocalStruct(Declaration def, LinkedHashMap<String, IRType> fields, int tempCounter) {
        this.parentDecl = def;
        this.fields = fields;
        this.tempCounter = tempCounter;
    }
    
    public void appendField(String name, IRType type){
        if(fields.containsKey(name)){
            throw new IllegalArgumentException(
                String.format("Field name %s already exists for %s.", name, parentDecl));
        }
        fields.put(name, type);
    }

    public String commaSeparatedFieldList(){
        StringBuffer sb = new StringBuffer();
        String delim = "";

        int paramNum = fields.size() - tempCounter;
        for (Map.Entry<String, IRType> e : fields.entrySet()) {
           if(paramNum == 0) break;

           sb.append(delim);
           sb.append(e.getValue().getName()); 
           sb.append(" ");
           sb.append(e.getKey());
            
           delim = ", ";

           paramNum -= 1;
        }
        return sb.toString();
    }

//    public void prependFields(LinkedHashMap<String, IRType> firstFields){
//        LinkedHashMap<String, IRType> newMap = new LinkedHashMap<>(firstFields);
//        newMap.putAll(this.fields);
//        this.fields = newMap;
//    }

    public String addTemporary(IRType type, String name){
        name = "temp" + tempCounter + "_" + name;
        fields.put(name, type);
        tempCounter += 1;
        return parentDecl.getName() + "." + name;
    }

    @Override
    public String getName() {
        return parentDecl.getName();
    }

    @Override
    public int getSize() {
        return calcSize();
    }

    private int calcSize() {
        int s = 0;
        for (IRType type : fields.values()) {
           s += type.getSize() ;
        }

        return s;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }

    // TODO these two would be simpler, if parent params, owned params, and temps were stored separately
    public LocalStruct restrictToOwnedVars() {
        LinkedHashMap<String, IRType> owned = new LinkedHashMap<>();
        for (Map.Entry<String, IRType> f : fields.entrySet()) {
            if(f.getValue() instanceof Struct){
                continue;
            }

            owned.put(f.getKey(), f.getValue());
        }
        return new LocalStruct(parentDecl, owned, tempCounter);
    }

    public LocalStruct restrictToOwnedParameters() {
        LocalStruct owned = restrictToOwnedVars();
        LinkedHashMap<String, IRType> params = new LinkedHashMap<>();
        int paramNum = owned.getFields().size() - tempCounter;
        for (Map.Entry<String, IRType> entry :  owned.getFields().entrySet()) {
            if(paramNum == 0)
                break;

            params.put(entry.getKey(), entry.getValue());
            paramNum -= 1;
        }
        return new LocalStruct(parentDecl, params, tempCounter);
    }

    @Override
    public String toString() {
        return "LocalStruct [fields=" + fields + "]";
    }

}
