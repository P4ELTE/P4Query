package p4query.applications.smc.hir.typing;

import java.util.LinkedHashMap;

public interface Composite extends IRType {

    public LinkedHashMap<String, IRType> getFields();
    
}
