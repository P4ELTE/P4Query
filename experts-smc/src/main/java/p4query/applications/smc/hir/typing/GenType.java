package p4query.applications.smc.hir.typing;

import p4query.applications.smc.hir.exprs.Expression;

public class GenType implements IRType {


    private String name;
    private int size;
    private Expression origin;

    public GenType(String name, int size, Expression origin) {
        this.name = name;
        this.size = size;
        this.origin = origin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "GenType [name=" + name + ", size=" + size + "]";
    }

    
}
