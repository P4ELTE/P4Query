package p4query.applications.smc.lir.typing;

public class UnresolvedNameLabel extends Label {

    private String namespace;
    private String name;

    public UnresolvedNameLabel(String namespace, String name, String comment) {
        super(-1, comment);
        this.name = name;
        this.namespace = namespace;
    }

    @Override
    public String toString() {
            return String.format("UnresolvedNameLabel[%s::%s, '%s']", namespace, name, comment);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toHumanReadable() {
        return integer + ": unresolved label to " + comment;
    }

}
