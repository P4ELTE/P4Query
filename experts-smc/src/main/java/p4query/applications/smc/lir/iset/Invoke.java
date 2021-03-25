package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.Label;
import p4query.applications.smc.lir.typing.Size;

public class Invoke implements StackInstruction, InterProcJumping {
    private Size noArgs;
    private Label dest;

    public Invoke(Label dest, Size noArgs) {
       this.dest = dest;
       this.noArgs = noArgs;
    }

    @Override
    public String toString() {
        return "Invoke("+dest+", " + noArgs + ")";
    }

    public Label getDest() {
        return dest;
    }

    public void setDest(Label target) {
        this.dest = target;
    }

    @Override
    public String toHumanReadable() {
        return "invoke " + dest.getInteger() +  " " + noArgs.getInteger() + "\t\t // " + dest.toHumanReadable() + ", " + noArgs.toHumanReadable();
    }
    
}
