package p4query.applications.smc.lir.typing;

public class Size extends IntegerBasedType {

    public Size(int integer, String comment) {
        super(integer, comment);
    }

//    @Override
//    public String toString() {
//        return String.format("Size [%s, %s]", integer , comment);
//    }
    
    @Override
    public String toHumanReadable() {
        return integer + ": size of " + comment;
    }
}
