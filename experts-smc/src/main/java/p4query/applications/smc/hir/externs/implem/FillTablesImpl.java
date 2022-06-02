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
package p4query.applications.smc.hir.externs.implem;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import p4query.applications.smc.Header;
import p4query.applications.smc.TableEntry;
import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.hir.exprs.Expression;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.TableDeclaration;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.DerefTop;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Return;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Sub;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;


public class FillTablesImpl extends ExternDefinition {

    private Map<String, TableEntry[]> tableContents;
    
    public FillTablesImpl(final Declaration iface, final Map<String, TableEntry[]> tableContents) throws UnableToLinkDeclaration {
        super(iface, "fill_tables", "stdlib");
        this.tableContents = tableContents;
    }

    private Builder instLayout;
    private CompilerState state;

    public void init(final CompilerState state) {
        this.instLayout = state.getInstLayout();
        this.state = state;
        for(Declaration d : state.getDeclarations()){
            TableEntry[] entries = tableContents.get(d.getName());
            if(entries != null){
                createFields(d, entries);
            }
        }
    }

    private void createFields(Declaration d, TableEntry[] entries) {
        TableDeclaration td = (TableDeclaration) d;
        List<Expression> keys = td.getKeys();
        for (int i = 0; i < entries.length; i++) {
            int numPatterns = entries[i].getPatterns().size();
            if(numPatterns != keys.size()){
                throw
                    new IllegalStateException(
                            String.format("Table entry has wrong number of keys, expected %d, got %d  (table=%s, entry=%s)",
                                          keys.size(), numPatterns, d.getName(), entries[i]));
            }
            for (int j = 0; j < numPatterns; j++) {
                int expectedSize = keys.get(j).getTypeHint().getSize();
                BigInteger patt = entries[i].getPatterns().get(j);
                Header.bigIntegerToBitArray(patt, expectedSize); // not used, but it will throw an exception if the number does not fit
                d.getLocals().appendField(entrySelector(i, j),
                                          new GenType("key_pattern",expectedSize, null));
            }
        }
    }

    public static String entrySelector(int i, int j) {
        return "ENTRY[" + i + "," + j + "]";
    }

    @Override
    public List<StackInstruction> compileToLIR(final GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();
        insts.add(super.openDefinition());

        for(Declaration d : state.getDeclarations()){
            TableEntry[] entries = tableContents.get(d.getName());
            if(entries != null){
                insts.add(new Comment("Filling table " + d.getName()));
                insts.addAll(fillFields(global,d, entries));
            }
        }

        StackInstruction exit0 = new Const(new Int(0, getDeclaration().getName() + " terminates with status OK"));
        insts.add(exit0);
        insts.add(new Return(new LocalAddress(0, ""), new Size(1, "")));
        insts.add(new Comment(" "));
        insts.add(super.closeDefinition());

        instLayout.registerProc(getDeclaration(), insts.getFirst(), exit0);
        return insts;
    }

    private LinkedList<StackInstruction> fillFields(final GlobalMemoryLayout global, Declaration d, TableEntry[] entries) {
        LinkedList<StackInstruction> insts = new LinkedList<>();

        TableDeclaration td = (TableDeclaration) d;
        List<Expression> keys = td.getKeys();
        for (int i = 0; i < entries.length; i++) {
            insts.add(new Comment(td.getName() + " / Entry " + i));
            int numPatterns = entries[i].getPatterns().size();
            for (int j = 0; j < numPatterns; j++) {
                Expression key = keys.get(j);

                insts.add(new Comment(td.getName() + " / Entry " + i + " / Key " + key.toP4Syntax()));

                int expectedSize = key.getTypeHint().getSize();
                BigInteger patt = entries[i].getPatterns().get(j);
                int[] bits = Header.bigIntegerToBitArray(patt, expectedSize);

                for (int bit : bits) {
                    insts.add(new Const(new Int(bit, "")));
                }

                insts.add(new DerefTop());
                insts.add(new Const(new Size(bits.length - 1, td.getName() + "." + entrySelector(i,j) + "  - 1")));
                insts.add(new Sub());

                Integer addr = global.lookupSegmentByName(td.getName() + "."+ entrySelector(i,j)).getAddress();
                insts.add(new Const(new GlobalAddress(addr, td.getName() + "." + entrySelector(i,j))));

                insts.add(new Const(new Size(bits.length, td.getName() + "." + entrySelector(i,j))));
                insts.add(new Invoke(new UnresolvedNameLabel("stdlib", "memcpy", ""), new Size(3, "src, dst, length")));
            }
        }
        
        return insts;
    }

}
