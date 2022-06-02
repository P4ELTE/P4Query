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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.Fraction;

// A probalistic instruction is a group of instructions that share the same label. 
// When execution reaches that label, one of the instructions is randomly selected (according to the given distribution).
// To handle groups of instructions, use probabilistic GOTOs.
public class ProbabilisticInstruction implements StackInstruction {

    private LinkedHashMap<StackInstruction, Fraction> probabilities = new LinkedHashMap<>();

    private ProbabilisticInstruction() {
    }

    public static class Builder {
        ProbabilisticInstruction pinst = new ProbabilisticInstruction();

        public Builder() {

        }

        public void addInstruction(Fraction probability, StackInstruction inst) {
            pinst.probabilities.put(inst, probability);
        }

        // TODO this should make a deep copy
        public ProbabilisticInstruction build() {
            Fraction sumProb = Fraction.ZERO;
            for (Fraction p : pinst.probabilities.values()) {
                sumProb = sumProb.add(p);
            }
            sumProb = sumProb.reduce();
            if(!sumProb.equals(Fraction.ONE)){
                throw 
                    new IllegalArgumentException(
                        String.format(
                            "Cannot build %s, the probabilitites don't sum up to 1. (sum(%s) == %s)",
                            ProbabilisticInstruction.class,
                            pinst.probabilities.values(),
                            sumProb));
            }
            return pinst;
        }
    }

    @Override
    public String toHumanReadable() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ ");
        String delim = "";
        for (Map.Entry<StackInstruction, Fraction> entry  : probabilities.entrySet()) {
            sb.append(delim);
            sb.append(entry.getValue());
            sb.append(" -> ");
            sb.append(entry.getKey().toHumanReadable());
            delim = ", ";
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override
    public void toPrism(PrintStream os) {
        String delim = " ";
        for (Map.Entry<StackInstruction, Fraction> entry  : probabilities.entrySet()) {

            os.println(delim);
            os.println(" " + entry.getValue() + " : ");
            entry.getKey().toPrism(os);
            delim = " + ";
        }
    }

    public List<StackInstruction> getInstructions(){
        return new LinkedList<>(probabilities.keySet());

    }
    
}
