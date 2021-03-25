package p4query.applications.smc.lir.typing;

public class UnresolvedVertexLabel extends Label {

    private Object srcVertex;

    public UnresolvedVertexLabel(Object srcVertex, String comment) {
        super(-1, comment);
        this.srcVertex = srcVertex;
    }

    public Object getVertex(){
        return srcVertex;
    }


    @Override
    public String toString() {
        return String.format("UnresolvedVertexLabel[%s, '%s']", srcVertex, comment);
    }
    
    @Override
    public String toHumanReadable() {
        return integer + ": unresolved label to " + comment;
    }
}
