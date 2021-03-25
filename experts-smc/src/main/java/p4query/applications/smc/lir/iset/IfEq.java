package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

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

}
