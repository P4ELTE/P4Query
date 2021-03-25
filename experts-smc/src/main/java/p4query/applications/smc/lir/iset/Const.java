package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.IntegerBasedType;

public class Const implements StackInstruction {
    private IntegerBasedType n;

    public Const(IntegerBasedType n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "Const(" + n + ")";
    }

    @Override
    public String toHumanReadable() {
        return "const " + n.getInteger() + "\t\t //" + n.toHumanReadable();
    }

}
