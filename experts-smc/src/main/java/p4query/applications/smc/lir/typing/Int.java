package p4query.applications.smc.lir.typing;

public class Int extends IntegerBasedType{

    public Int(int integer, String comment) {
        super(integer, comment);
    }

    @Override
    public String toHumanReadable() {
        return integer + ": " + comment;
    }
    
}
