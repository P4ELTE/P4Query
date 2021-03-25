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
package p4query.applications.smc.lir;

import java.io.PrintStream;

import p4query.applications.smc.lir.iset.Exit;
import p4query.applications.smc.lir.iset.StackInstruction;

public class LabelledStackInstruction {
    private StackInstruction inst;
    private Integer label;

    public LabelledStackInstruction(Integer label, StackInstruction inst) {
        this.label = label;
        this.inst = inst;
    }

    public StackInstruction getInst() {
        return inst;
    }

    public Integer getLabel() {
        return label;
    }

    public String toHumanReadable(){
        if(label == null){
            return "// " + inst.toHumanReadable();
        }
        return label + ":  \t" + inst.toHumanReadable();
    }
}
