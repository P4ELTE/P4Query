/**
 * Copyright 2020-2021, Eötvös Loránd University.
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
 */
package p4query.applications.smc.hir.exprs;

import java.util.LinkedList;
import java.util.List;

import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.Segment;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;

public abstract class StorageReference implements Expression {

	abstract public String getFirstFieldName();

    abstract public int getSizeOffset();


    abstract public String getTailFields();

    abstract protected List<String> getFieldList();

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global ) {
        List<StackInstruction> insts = new LinkedList<>();

        Segment segm = findSegment(local, global);

        int sourceAddr = segm.getAddress();

        List<StackInstruction> loadInsts = new LinkedList<>();
        if(segm.isRelativeAddressing()){
            LocalAddress sourceAddrWr = new LocalAddress(sourceAddr, this.getFirstFieldName());
            loadInsts.add(new Load(sourceAddrWr));
        } else {
            GlobalAddress sourceAddrWr = new GlobalAddress(sourceAddr, this.getFirstFieldName());
            loadInsts.add(new Const(sourceAddrWr));
            loadInsts.add(new GetField());
        }

        Size offset = new Size(this.getSizeOffset(), this.getTailFields());

        if(this.getSizeOffset() != 0){
            insts.add(new Comment(this.toP4Syntax()));
            insts.addAll(loadInsts);
            insts.add(new Const(offset));
            insts.add(new Add());
        } else {
            insts.addAll(loadInsts);
        }

        return insts;
    }

    public Segment findSegment(LocalMemoryLayout local, GlobalMemoryLayout global) {
        Segment segm = local.lookupSegmentByName(this.getFirstFieldName());

        if(segm == null){
            segm = global.lookupSegmentByName(this.getFirstFieldName());
        }

        if(segm == null){
            System.out.println(local);
            System.out.println(this);
            throw new IllegalStateException(this.getFirstFieldName() + " is not in local nor global memory layout.");
        }
        return segm;
    }

}
