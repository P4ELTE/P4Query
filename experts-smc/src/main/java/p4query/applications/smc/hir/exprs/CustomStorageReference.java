package p4query.applications.smc.hir.exprs;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.typing.IRType;

public class CustomStorageReference extends StorageReference {

    private String name;
    private Expression origin;
    private IRType type;

    public CustomStorageReference(String vClass, String name, IRType type, Expression origin) {
        this.name = name;
        this.type = type;
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "CustomStorageReference [name=" + name + ", type=" + type + "]";
    }

    @Override
    public String getFirstFieldName() {
        return name;
    }

    @Override
    public int getSizeOffset() {
        return 0;
    }

    @Override
    public int getSizeHint() {
        return type.getSize();
    }

    @Override
    public String toP4Syntax() {
        return name;
    }

    @Override
    public String getTailFields() {
        return "";
    }

    @Override
    public StorageReference getStorageReference() {
        return this;
    }

    @Override
    protected List<String> getFieldList() {
        return Arrays.asList(name);
    }

    

    
}
