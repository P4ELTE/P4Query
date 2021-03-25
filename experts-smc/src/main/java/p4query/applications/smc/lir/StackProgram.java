package p4query.applications.smc.lir;

import java.io.PrintStream;
import java.util.LinkedList;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;
import p4query.applications.smc.lir.iset.StackInstruction;

public class StackProgram {
   private GlobalMemoryLayout global;
   private InstructionLayout insts;

   public StackProgram(GlobalMemoryLayout global, InstructionLayout insts) {
      this.global = global;
      this.insts = insts;
   }

   public void toHumanReadable(PrintStream os){
      os.println(global.toHumanReadable());
      for (String s : insts.toHumanReadable()) {
         os.println(s);
      }
   }
}
