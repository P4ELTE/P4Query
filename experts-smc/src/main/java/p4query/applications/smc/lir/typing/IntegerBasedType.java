package p4query.applications.smc.lir.typing;

public abstract class IntegerBasedType implements LIRType {
    protected int integer;
    protected String comment;

    public IntegerBasedType(int integer, String comment){ 
        this.integer = integer;
        this.comment = comment;
    }

    public Integer getInteger(){
        return integer;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + integer + ", '" + comment +"']";
    }

    @Override
    public String getComment(){
        return comment;
    }
    
}
