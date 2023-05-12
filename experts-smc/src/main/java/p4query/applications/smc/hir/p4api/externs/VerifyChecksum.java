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

public class VerifyChecksum extends ExternDeclaration {

    public VerifyChecksum() {
        super("verify_checksum", null);
        local.appendField("condition", new GenType("::update_checksum/condition", 1, null));
        local.appendField("data", new GenType("::update_checksum/data", 1, null));
        local.appendField("checksum", new GenType("::update_checksum/checksum", 1, null));
        local.appendField("algo", new GenType("::update_checksum/algo", 1, null));
    }
}