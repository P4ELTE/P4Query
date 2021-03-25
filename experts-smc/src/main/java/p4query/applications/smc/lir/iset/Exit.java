package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;

public class Exit implements StackInstruction {

    public Exit() {
    }

    @Override
    public String toString() {
        return "Exit()";
    }

    @Override
    public String toHumanReadable() {
        return "exit"; 
    }

}
