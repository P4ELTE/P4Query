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
