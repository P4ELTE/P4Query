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
import p4query.applications.smc.lir.typing.Size;

public class Return implements StackInstruction {
    private LocalAddress retVal;
    private Size retValLen;

    public Return(LocalAddress retVal, Size retValLen) {
        this.retVal = retVal;
        this.retValLen = retValLen;
    }

    @Override
    public String toString() {
        return "Return("+ retValLen + ")";
    }

    @Override
    public String toHumanReadable() {
        return "return "; 
    }

}
