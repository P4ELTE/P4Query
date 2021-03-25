package p4query.applications.smc.hir;

import p4query.applications.smc.hir.typing.IRType;

public class Segment {
    private final IRType type;
    private final Integer address;
    private final String prefix;
    private final String name;
    private boolean relativeAddressing;

    public Segment(final IRType type, final int address, String prefix, String fieldName, boolean relativeAddressing){
        this.type = type;
        this.address = address;
        this.prefix = prefix;
        this.name = fieldName;
        this.relativeAddressing = relativeAddressing;
    }

    @Override
    public String toString() {
        return "Segment [name=" + name + ", prefix=" + prefix + ", address=" + address + ", size=" + type.getSize() + "]";
    }
    public IRType getType() {
        return type;
    }

    public Integer getAddress() {
        return address;
    }
    public Boolean isRelativeAddressing() {
        return relativeAddressing;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }


}