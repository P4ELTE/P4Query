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
package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.IntegerBasedType;

public class Alloc implements StackInstruction {
    private IntegerBasedType n;

    public Alloc(IntegerBasedType n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "Alloc(" + n + ")";
    }

    @Override
    public String toHumanReadable() {
        return "alloc " + n.getInteger() + "\t\t //" + n.toHumanReadable();
    }

    @Override
    public void toPrism(PrintStream os) {
        os.println("  // " + n.toHumanReadable());
        os.println("  (op' = OP_ALLOC) &");
        os.println("  (x1' = " + n.getInteger() + ")");
    }

    


}
