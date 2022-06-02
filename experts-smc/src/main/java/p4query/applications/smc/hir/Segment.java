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

import p4query.applications.smc.hir.typing.IRType;

public class Segment {
    private final IRType type;
    private final Integer address;
    private final String prefix;
    private final String name;
    private boolean relativeAddressing;

    public Segment(final IRType type, final int address, String prefix, String fieldName, boolean relativeAddressing){
        this.type = type;
        this.address = address;
        this.prefix = prefix;
        this.name = fieldName;
        this.relativeAddressing = relativeAddressing;
    }

    @Override
    public String toString() {
        return "Segment [name=" + name + ", prefix=" + prefix + ", address=" + address + ", size=" + type.getSize() + "]";
    }
    public IRType getType() {
        return type;
    }

    public Integer getAddress() {
        return address;
    }
    public Boolean isRelativeAddressing() {
        return relativeAddressing;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }


}