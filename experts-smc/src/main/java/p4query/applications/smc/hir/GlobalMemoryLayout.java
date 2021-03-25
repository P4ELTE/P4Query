package p4query.applications.smc.hir;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import p4query.applications.smc.hir.exprs.P4StorageReference;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.GlobalStruct;
import p4query.applications.smc.hir.typing.IRType;

// NOTE in P4 structs declaration also initializes, so struct types and struct names are the same
// NOTE I store global constanst in a struct-like "global", and there is only one
public class GlobalMemoryLayout  {

    private final List<Segment> layout = new LinkedList<>();
    private final LinkedHashMap<String, Segment> index = new LinkedHashMap<>();

	public GlobalMemoryLayout(IRType global) {
        this(Arrays.asList(global));
    }

	public GlobalMemoryLayout(final List<IRType> globals) {

        int currAddr = 0;
        for (IRType type : globals) {
            createLayout(type, currAddr, "", type.getName());
            currAddr += type.getSize();
        }
    }

    @Override
    public String toString() {
        return "GlobalMemoryLayout [index={" + toHumanReadable() + "}]";
    }

    public String toHumanReadable() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (Map.Entry<String, Segment> e : index.entrySet()) {
           sb.append("  ");
           sb.append(e.getKey()) ;
           sb.append("=");
           sb.append(e.getValue().getAddress()); 
           sb.append(" (");
           sb.append(e.getValue().getType().getSize()); 
           sb.append(")");
           sb.append("," + System.lineSeparator());
        }
        return sb.toString();
    }
    
    // NOTE: this is recursive, but structs are expected to be shallow 
    private void createLayout(IRType type, int address, String prefix, String fieldName){
        Segment inst = new Segment(type, address, prefix, fieldName, false);
        addToLayout(inst);

        if(type instanceof Composite){
            Composite structType = (Composite) inst.getType();
            int currAddr = address;
            for (Map.Entry<String, IRType> field : structType.getFields().entrySet()) {
                IRType subFieldType = field.getValue();
                String subFieldName = field.getKey();
                String prefix2 = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;
                createLayout(subFieldType, currAddr, prefix2, subFieldName);
                currAddr += subFieldType.getSize();
            }
        }         
    }

    private void addToLayout(Segment inst) {
        layout.add(inst);
        String prefix2 = inst.getPrefix().isEmpty() ? "" : inst.getPrefix() + ".";
        index.put(prefix2 + inst.getName(), inst);
    }

    public Segment lookupSegmentByName(String name){
        Segment res = index.get(name);
        if(res != null ) return res;

        // TODO find a nicer solution to play together with GlobalStruct
        return index.get("GLOBAL." + name);
    }

}
