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

import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.lir.typing.Label;

public class IfEq implements StackInstruction, IntraProcJumping {
    private Label dest;

    public IfEq(Label dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "IfEq(" + dest + ")";
    }

    public Label getDest() {
        return dest;
    }

    public void setDest(Label dest) {
        this.dest = dest;
    }

    @Override
    public String toHumanReadable() {
        return "ifeq " + dest.getInteger() + "\t\t //" + dest.toHumanReadable();
    }

    @Override
    public void toPrism(PrintStream os) {
        os.println("  // " + dest.toHumanReadable());
        os.println("  (op' = OP_IFEQ) &");
        os.println("  (x1' = " + dest.getInteger() + ")");
    }


}
