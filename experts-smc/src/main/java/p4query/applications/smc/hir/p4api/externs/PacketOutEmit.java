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
package p4query.applications.smc.hir.p4api.externs;

import p4query.applications.smc.hir.typing.GenType;

public class PacketOutEmit extends ExternDeclaration {

    public PacketOutEmit() {
        super("emit", "packet_out");
        this.local.appendField("packet", new GenType("packet_out::emit/packet", 1, null));
        this.local.appendField("hdr", new GenType("packet_out::emit/hdr", 1, null));
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getName() {
        return name;
    }


}
