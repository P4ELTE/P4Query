package p4query.applications.smc.hir.externs;

import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.iset.ProcedureDone;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;

public class IsValid extends ExternDefinition {

    private InstructionLayout.Builder instLayout;

    public IsValid(InstructionLayout.Builder instLayout) {
        super("isValid", "core");
        this.instLayout = instLayout;
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
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("define " + name + "(hdr)"));
//        insts.add(new Load(new LocalAddress(0, "hdr.valid")));
//        insts.add(new GetField());

        insts.add(new Const(new Int(1, "this implementation of isValid always return true")));
        insts.add(new Return(new LocalAddress(1, ""), new Size(1, "") ));
        insts.add(new Comment(" "));

        instLayout.registerProc(this, insts.getFirst());

        return insts;
    }

    @Override
    public LocalStruct getLocal() {
        return this.local;
    }
    
}
