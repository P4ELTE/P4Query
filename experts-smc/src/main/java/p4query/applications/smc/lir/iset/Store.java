package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

import p4query.applications.smc.lir.typing.LocalAddress;

public class Store implements StackInstruction {

    private LocalAddress target;

    // this is for storing literals. (invoke memcpy for longer data)
    public Store(LocalAddress target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Store [target=" + target + "]";
    }

    @Override
    public String toHumanReadable() {
        return "store " + target.getInteger() + "\t\t //" + target.toHumanReadable();
    }
}
