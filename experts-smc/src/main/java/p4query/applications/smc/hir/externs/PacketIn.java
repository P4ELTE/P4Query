package p4query.applications.smc.hir.externs;

import java.util.LinkedHashMap;


import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;

public class PacketIn implements Composite {

    @Override
    public String getName() {
        return "packet_in";
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return new LinkedHashMap<>();
    }

    
}
