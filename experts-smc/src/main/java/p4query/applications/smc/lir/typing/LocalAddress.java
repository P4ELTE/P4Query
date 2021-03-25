package p4query.applications.smc.lir.typing;

public class LocalAddress extends IntegerBasedType {

    public LocalAddress(int integer, String comment) {
        super(integer, comment);
    }

    @Override
    public String toHumanReadable() {
        return integer + ": local address of " + comment;
    }
}
