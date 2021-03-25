package p4query.applications.smc.lir.typing;

public class GlobalAddress extends IntegerBasedType {

    public GlobalAddress(int n, String comment) {
        super(n, comment);
    }

    @Override
    public String toHumanReadable() {
        return "global address of " + comment;
    }

}
