package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.LocalAddress;

public class Load implements StackInstruction {
    private LocalAddress src;

    public Load(LocalAddress src) {
        this.src = src;
   }

   @Override
   public String toString() {
       return "Load("+ src+")";
   }

    @Override
    public String toHumanReadable() {
        return "load " + src.getInteger() + "\t\t // " + src.toHumanReadable();
    }
}

