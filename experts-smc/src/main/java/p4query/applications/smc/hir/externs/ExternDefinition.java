package p4query.applications.smc.hir.externs;

import java.util.List;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.lir.iset.StackInstruction;

public abstract class ExternDefinition implements Definition {
    protected String name;
    protected String namespace;
    protected LocalStruct local;

    private ExternDefinition(){}

    protected ExternDefinition(String name, String namespace){
        this.name = name;
        this.namespace = namespace;
        this.local = new LocalStruct(this);

    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalStruct getLocal() {
        return this.local;
    }

    @Override
    public String addTemporary(IRType type) {
        return local.addTemporary(type);
    }
    
}
