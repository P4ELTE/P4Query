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

public class CounterCount extends ExternDeclaration {

    public CounterCount() {
        super("count", "counter");
        this.local.appendField("counter", new GenType("counter::count/counter", 1, null));
        this.local.appendField("index", new GenType("counter::count/index", 32, null));
    }
}
