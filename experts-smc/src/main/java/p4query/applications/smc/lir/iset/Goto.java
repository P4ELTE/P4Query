package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.Label;

public class Goto implements StackInstruction, IntraProcJumping {

    private Label dest;

    public Goto(Label dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "Goto(" + dest + ")";
    }

    public Label getDest() {
        return dest;
    }

    public void setDest(Label dest) {
        this.dest = dest;
    }

    @Override
    public String toHumanReadable() {
        return "goto " + dest.getInteger() + "\t\t /* " + dest.toHumanReadable() + " */";
    }
    
}
