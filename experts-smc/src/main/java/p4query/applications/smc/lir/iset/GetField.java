package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

public class GetField implements StackInstruction {
    @Override
    public String toString() {
        return "GetField()";
    }

    @Override
    public String toHumanReadable() {
        return "getfield";
    }
}
