package p4query.applications.smc.lir.typing;

import java.util.List;

import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;

public class UnresolvedInstructionLabel extends Label {

    private StackInstruction inst;

    public UnresolvedInstructionLabel(StackInstruction inst, String comment) {
        super(-1, comment);
        this.inst = inst;
    }

    public StackInstruction getTarget(){
        return inst;
    }


    @Override
    public String toString() {
        return String.format("UnresolvedInstructionLabel[%s, '%s']", inst, comment);
    }
    
    @Override
    public String toHumanReadable() {
        return integer + ": unresolved label to " + comment;
    }
}
