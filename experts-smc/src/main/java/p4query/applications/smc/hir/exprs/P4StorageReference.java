package p4query.applications.smc.hir.exprs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.beust.jcommander.Strings;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.IRType;
import p4query.ontology.Dom;

public class P4StorageReference extends StorageReference {
    private Vertex src;
    private GraphTraversalSource g;
    private LinkedList<String> fieldList = new LinkedList<>();
    private Map<String, IRType> fieldTypes = new HashMap<>();
    private IRType.SingletonFactory typeFactory;

    P4StorageReference(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory) {
        this.g = g;
        this.src = src;
        this.typeFactory = typeFactory;

        String typeType = (String) g.V(src).values(Dom.Syn.V.CLASS).next();

        if(!(typeType.equals("LvalueContext") || typeType.equals("ExpressionContext")) )
            throw new IllegalArgumentException(
                String.format("Store reference cannot be initilized on %s vertex %s.", typeType, src));

        fillFields();

    }


    @Override
    public String getFirstFieldName(){
        return fieldList.getFirst();
    }

    @Override
    public String getTailFields(){
        LinkedList<String> tail = new LinkedList<>(fieldList);
        tail.removeFirst();
        return Strings.join(".", tail);
    }


    @Override
    public String toString() {
        return "StorageReference [fieldList=" + fieldList + ", fieldTypes=" + fieldTypes + "]";
    }

    // how much we need to go from the source address to reach the address of the last field?
    @Override
    public int getSizeOffset(){
        int currAddr = 0;

        Iterator<String> it = fieldList.iterator();
        String curr = it.next();
        while(it.hasNext()){
            IRType type = fieldTypes.get(curr);
            if(!(type instanceof Composite)) 
                return currAddr;

            Composite comp = (Composite) type;

            String next = it.next();

            boolean found = false;
            for (Map.Entry<String, IRType> compField : comp.getFields().entrySet()) {
                if(compField.getKey().equals(next)){
                    found = true;
                    break;
                } 

                currAddr += compField.getValue().getSize();
            }
            if(!found)
                throw new IllegalStateException("Field " + next + " not in " + comp);

            curr =  next;
        }

        return currAddr;
    }

    // what is the size of the pointed storage field
    @Override
    public int getSizeHint(){
        return fieldTypes.get(fieldList.getLast()).getSize();
    }

    private void fillFields() {
        List<Map<String, Object>> fields = 
            g.V(src)
             .repeat(__.outE(Dom.SYN)
                       .not(__.has(Dom.Syn.E.RULE, "DOT"))
                       .order().by(Dom.Syn.E.ORD, Order.desc)
                       .inV())
             .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .project("vertex", "name")
             .by(__.identity())
             .by(__.values("value"))
             .toList();

        Collections.reverse(fields);

        for (Map<String,Object> map : fields) {
            Vertex fv = (Vertex) map.get("vertex") ;
            String name = (String) map.get("name") ;

            Vertex typeVert;
            try { 
                typeVert =
                    g.V(fv).inE(Dom.SYMBOL)
                           .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                           .outV()
                           .outE(Dom.SYMBOL)
                           .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                           .inV()
                           // NOTE: optional will run in case of structs. it will not run in case of base types.
                           .optional(
                                __
                                .inE(Dom.SYMBOL)
                                .has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                                .outV())
                           .next();

            } catch(NoSuchElementException e){
                System.err.println(g.V(fv).elementMap().next());

                throw new IllegalStateException(
                    String.format("No type information found for name %s (vertex %s)", name, fv));
            }

            fieldList.add(name);
            if(typeVert != null){
                IRType type = typeFactory.create(typeVert);
                if(fieldTypes.containsKey(name))
                    throw new IllegalStateException("Field name " + name + " is already stored. Dot expressions with duplicate field names are not supported yet.");
                fieldTypes.put(name, type);
            }
        }
    }

    @Override
    public String toP4Syntax() {
        return Strings.join(".", fieldList);
    }

    @Override
    public StorageReference getStorageReference() {
        return this;
    }

    @Override
    protected List<String> getFieldList() {
        return fieldList;
    }

}
