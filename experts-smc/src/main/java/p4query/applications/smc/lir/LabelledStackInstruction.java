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
package p4query.applications.smc.lir;

import java.io.PrintStream;
import java.util.List;

import p4query.applications.smc.lir.iset.ProbabilisticInstruction;
import p4query.applications.smc.lir.iset.StackInstruction;

public class LabelledStackInstruction {
    private StackInstruction inst;
    private Integer label;
    private boolean nondet;

    public LabelledStackInstruction(Integer label, StackInstruction inst, boolean nondet) {
        this.label = label;
        this.inst = inst;

        this.nondet = nondet;
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

        if(nondet && inst instanceof ProbabilisticInstruction){
            return toHumanReadableNonDet();
        }
        return label + ":  \t" + inst.toHumanReadable();
    }

    public void toPrism(PrintStream os){
        if(label == null){
            os.println("// " + inst.toHumanReadable());
            return;
        }

        if(nondet && inst instanceof ProbabilisticInstruction){
            toPrismNonDet(os);
            return;
        }
//        if(inst instanceof Exit){
//            os.println("// " + label + ": " + inst.toHumanReadable());
//            return;
//        }
        os.println("[] (eip=" + label + " & op=NO_OP) ->");
        inst.toPrism(os);
        os.println("  ;");
        os.println();
    }

    // TODO: this a hack for printing non-deterministic instructions. the problem is that in PRISM these have to be represented with multiple labelled instructions, but we only handle labels here in LabelledStackInstruction (so extending ProbabilisticInstruction or creating another class doesn't seem to solve the issue)
    private String toHumanReadableNonDet() {
        ProbabilisticInstruction pinst = (ProbabilisticInstruction) inst;

        List<StackInstruction> subinsts = pinst.getInstructions();

        StringBuffer sb = new StringBuffer();
        sb.append(label + ":  \t" + "nondet { ");
        String delim = "";
        for (StackInstruction entry  : subinsts) {
            sb.append(delim);
            sb.append(entry.toHumanReadable());
            delim = ", ";
        }
        sb.append(" }");
        return sb.toString();
    }

    private void toPrismNonDet(PrintStream os) {
        ProbabilisticInstruction pinst = (ProbabilisticInstruction) inst;
        List<StackInstruction> subinsts = pinst.getInstructions();

        os.println("// NOTE: instruction " + label + " is intentionally non-deterministic");
        // repeat the same label for each instruction
        for (StackInstruction entry  : subinsts) {
            os.println("[] (eip=" + label + " & op=NO_OP) ->");
            entry.toPrism(os);
            os.println("  ;");
            os.println();
        }
    }
    
}
