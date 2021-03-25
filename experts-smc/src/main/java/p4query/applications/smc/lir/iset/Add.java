package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

public class Add implements StackInstruction {

    @Override
    public String toString() {
        return "Add()";
    }

    @Override
    public String toHumanReadable() {
        return "add";
    }
}
