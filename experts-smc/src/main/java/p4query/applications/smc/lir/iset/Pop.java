package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

public class Pop implements StackInstruction {

    @Override
    public String toString() {
        return "Pop()";
    }

    @Override
    public String toHumanReadable() {
        return "pop";
    }
}
    

