package p4query.applications.smc.hir.typing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.ProcedureDefinition;

public class LocalStruct implements Composite {

    private LinkedHashMap<String, IRType> fields = new LinkedHashMap<>(); // stored in input order
    private Definition parentDef;

    private int tempCounter = 0;

    public LocalStruct(Definition def) {
        this.parentDef = def;
    }

    private LocalStruct(Definition def, LinkedHashMap<String, IRType> fields, int tempCounter) {
        this.parentDef = def;
        this.fields = fields;
        this.tempCounter = tempCounter;
    }
    
    public void appendField(String name, IRType type){
        if(fields.containsKey(name)){
            throw new IllegalArgumentException("field name already exists " + name);
        }
        fields.put(name, type);
    }

    public String commaSeparatedFieldList(){
        StringBuffer sb = new StringBuffer();
        String delim = "";
        for (Map.Entry<String, IRType> e : fields.entrySet()) {
           sb.append(delim);
           sb.append(e.getValue().getName()); 
           sb.append(" ");
           sb.append(e.getKey());
            
            delim = ", ";
        }
        return sb.toString();
    }

//    public void prependFields(LinkedHashMap<String, IRType> firstFields){
//        LinkedHashMap<String, IRType> newMap = new LinkedHashMap<>(firstFields);
//        newMap.putAll(this.fields);
//        this.fields = newMap;
//    }

    public String addTemporary(IRType type){
        String name = "TEMP_" + tempCounter;
        tempCounter += 1;
        fields.put(name, type);
        return name;
    }

    @Override
    public String getName() {
        return parentDef.getName();
    }

    @Override
    public int getSize() {
        return calcSize();
    }

    private int calcSize() {
        int s = 0;
        for (IRType type : fields.values()) {
           s += type.getSize() ;
        }

        return s;
    }

    @Override
    public LinkedHashMap<String, IRType> getFields() {
        return fields;
    }

    // TODO these two would be simpler, if parent params, owned params, and temps were stored separately
    public LocalStruct restrictToOwnedFields() {
        LinkedHashMap<String, IRType> owned = new LinkedHashMap<>();
        for (Map.Entry<String, IRType> f : fields.entrySet()) {
            if(f.getValue() instanceof Struct){
                continue;
            }
            owned.put(f.getKey(), f.getValue());
        }
        return new LocalStruct(parentDef, owned, tempCounter);
    }

    public LocalStruct restrictToOwnedParameters() {
        LocalStruct owned = restrictToOwnedFields();
        LinkedHashMap<String, IRType> params = new LinkedHashMap<>();
        int paramNum = owned.getFields().size() - tempCounter;
        for (Map.Entry<String, IRType> entry :  owned.getFields().entrySet()) {
            if(paramNum == 0)
                break;
            params.put(entry.getKey(), entry.getValue());
            paramNum -= 1;
        }
        return new LocalStruct(parentDef, params, tempCounter);
    }

    @Override
    public String toString() {
        return "LocalStruct [fields=" + fields + "]";
    }

    
}
