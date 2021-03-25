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
