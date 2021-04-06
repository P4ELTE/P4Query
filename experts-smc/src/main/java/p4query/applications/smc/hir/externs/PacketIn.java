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


import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;

public class PacketIn implements Composite {

    @Override
    public String getName() {
        return "packet_in";
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return new LinkedHashMap<>();
    }

    
}
