package p4query.applications.smc.lir.iset;

import java.io.PrintStream;
import java.math.BigDecimal;
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

    public List<StackInstruction> getInstructions(){
        return new LinkedList<>(probabilities.keySet());

    }
    
}
