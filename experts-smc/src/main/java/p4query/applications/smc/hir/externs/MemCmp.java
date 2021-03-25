package p4query.applications.smc.hir.externs;

import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.PutField;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;

public class MemCmp extends ExternDefinition {

    private Builder instLayout;

    public MemCmp(InstructionLayout.Builder instLayout) {
        super("memcmp", "stdlib");
        this.instLayout = instLayout;

//        for (Object obj : names) {
//            body.add(new Invoke(new UnresolvedNameLabel("", (String) obj, ""), new Size(0, "")));
//        }
    }

    // TODO this will need a new unresolvedlabel, for jumping to instructions

    @Override
    public List<StackInstruction> compileToLIR(GlobalMemoryLayout global) {
        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(new Comment("define " + name + "()"));

        insts.add(new Comment("TODO"));

        insts.add(new Const(new Int(0, getName() + " terminates with status OK")));
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "") ));
        insts.add(new Comment(" "));
        
        instLayout.registerProc(this, insts.getFirst());

        return insts;
    }
    
}
