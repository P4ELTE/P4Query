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
package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.LocalAddress;

public class Store implements StackInstruction {

    private LocalAddress target;

    // this is for storing literals. (invoke memcpy for longer data)
    public Store(LocalAddress target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Store [target=" + target + "]";
    }

    @Override
    public String toHumanReadable() {
        return "store " + target.getInteger() + "\t\t //" + target.toHumanReadable();
    }
}
