package p4query.applications.smc.hir.externs;

import java.util.LinkedHashMap;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.hir.typing.GlobalStruct;
import p4query.applications.smc.hir.typing.IRType;

public class PacketOut implements Composite {
    public static final int PTR_SIZE = 1;  // don't change this, it is dependent on th estack machine
    public static final int BUFFER_SIZE = 100;

    private LinkedHashMap<String, IRType> fields = new LinkedHashMap<>();

    public PacketOut(IRType.SingletonFactory factory){
        GenType ptrType = factory.create("PACKET_PTR", PTR_SIZE, null);
        fields.put("cursor", ptrType);

        GenType pbType = factory.create("PACKET_BUFFER", BUFFER_SIZE, null);
        fields.put("buffer", pbType);

    }  

    @Override
    public String getName() {
        return "packet_out";
    }

    @Override
    public int getSize() {
        return BUFFER_SIZE + 1;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }

    
}
