/**
 * Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Dániel Lukács, 2022
 */
package p4query.applications.smc.lir;

import java.io.PrintStream;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout;

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

   public void toPrism(PrintStream os) {
      insts.toPrism(os);
   };
}
