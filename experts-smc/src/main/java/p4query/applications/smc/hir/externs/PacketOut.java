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

import java.util.LinkedHashMap;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.hir.typing.GlobalStruct;
import p4query.applications.smc.hir.typing.IRType;

public class PacketOut implements Composite {
    public static final int PTR_SIZE = 1;  // don't change this, it is dependent on th estack machine
    public static final int BUFFER_SIZE = 100;

    private LinkedHashMap<String, IRType> fields = new LinkedHashMap<>();

    public PacketOut(IRType.SingletonFactory factory){
        GenType ptrType = factory.create("PACKET_PTR", PTR_SIZE, null);
        fields.put("cursor", ptrType);

        GenType pbType = factory.create("PACKET_BUFFER", BUFFER_SIZE, null);
        fields.put("buffer", pbType);

    }  

    @Override
    public String getName() {
        return "packet_out";
    }

    @Override
    public int getSize() {
        return BUFFER_SIZE + 1;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }

    
}
