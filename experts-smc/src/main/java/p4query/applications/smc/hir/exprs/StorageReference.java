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
package p4query.applications.smc.hir.exprs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.Segment;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.ProcedureDeclaration;
import p4query.applications.smc.hir.p4api.TableDeclaration;
import p4query.applications.smc.hir.typing.Composite;
import p4query.applications.smc.hir.typing.GenType;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Load;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.FieldIndex;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.ontology.Dom;

// a.k.a. lvalue
public abstract class StorageReference implements Expression {

	abstract public String getFirstFieldName();

    abstract public int getSizeOffset();

    abstract public Declaration getParentDecl();

    abstract public Declaration getParentControlDecl();

    abstract public String getTailFields();

    abstract protected List<String> getFieldList();

    abstract protected LinkedHashMap<String, IRType> getFields();


    protected Declaration findParentControlDecl(CompilerState state) {
        Vertex parentSrc;

        if(getParentDecl() instanceof ProcedureDeclaration){
            ProcedureDeclaration pd = (ProcedureDeclaration) getParentDecl();
            if(!state.getG().V(pd.getSrc()).has(Dom.Syn.V.CLASS, "ActionDeclarationContext").hasNext())
                return null;
            parentSrc = pd.getSrc();
        } else if(getParentDecl() instanceof TableDeclaration){
            TableDeclaration pd = (TableDeclaration) getParentDecl();
            parentSrc = pd.getSrc();
        } else {
            return null;
        }

        List<Vertex> maybeParNode = 
            state.getG().V(parentSrc)
                        .repeat(__.inE(Dom.SYN).outV())
                        .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))
                        .toList();

        if(maybeParNode.isEmpty())
            return null;

        for (Declaration cand : state.getDeclarations()) {
            if(!(cand instanceof ProcedureDeclaration))
                continue;

            if(((ProcedureDeclaration) cand).getSrc().equals(maybeParNode.get(0))){
                return cand;
            }
        }

        return null;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global ) {

// Address resolution of hdr.eth.ethType goes like: 
//
//   resolve(resolve(resolve(hdr) + n) + m), where:
//
//   * hdr stores the address of a list
//   * n is the index  of eth field in the hdr struct
//   * m is the index  of ethType field in the eth struct
//
// In RPN this is (hdr resolve n + resolve m + resolve)

        List<StackInstruction> insts = new LinkedList<>();

        if(getFields().size() > 1){
            insts.add(new Comment(this.toP4Syntax()));
        }

        Segment segm = findSegment(local, global);

        int sourceAddr = segm.getAddress();

        List<StackInstruction> loadInsts = new LinkedList<>();
        if(segm.isRelativeAddressing()){
            LocalAddress sourceAddrWr = new LocalAddress(sourceAddr, this.getFirstFieldName());
            loadInsts.add(new Load(sourceAddrWr)); // parameter stores a pointer pointing to a list. push it on the stack.
        } else {
            GlobalAddress sourceAddrWr = new GlobalAddress(sourceAddr, this.getFirstFieldName());
            loadInsts.add(new Const(sourceAddrWr)); // we know where the pointer is. push it on the stack.
        }
        insts.addAll(loadInsts);

        // note: getFields return ordered map
        Iterator<Map.Entry<String, IRType>> it = getFields().entrySet().iterator();
        IRType currType = it.next().getValue();
        while(it.hasNext()){
            Map.Entry<String, IRType> fi = it.next();
            if(currType instanceof Composite){
                int fieldIdx = new ArrayList<>(((Composite) currType).getFields().keySet()).indexOf(fi.getKey());
                insts.add(new Const(new FieldIndex(fieldIdx, fi.getKey())));
                insts.add(new Add()); // increment the pointer to find the list element corresponding to the field

                // the top now points to a list element, that is itself a pointer. it either points to a value, or to a list. read it out either way.
                insts.add(new GetField()); 

                // if the pointer pointed to a value, we now have a pointer to a value on top of the stack, and we are done.
                // if the pointer pointed to a list, then the loop goes on: we increment it to get the next field. 

            } else if(it.hasNext()){
                throw new IllegalStateException("Non-last field does not have struct type in dot expression " + this);
            }
            currType = fi.getValue();
        }
        
        if(getFields().size() > 1){
            insts.add(new Comment("end of " + this.toP4Syntax()));
        }
        /* 
        Size offset = new Size(this.getSizeOffset(), this.getTailFields() + " offset");

        if(this.getSizeOffset() != 0){
            insts.add(new Comment(this.toP4Syntax()));
            insts.addAll(loadInsts);
            insts.add(new Const(offset));
            insts.add(new Add());
        } else {
            insts.addAll(loadInsts);
        }
        */

        return insts;

        
    }

    public Segment findSegment(LocalMemoryLayout local, GlobalMemoryLayout global) {
        Segment segm = local.lookupSegmentByName(this.getFirstFieldName());

        if(segm == null){
            segm = global.lookupSegmentByName(this.getFirstFieldName());
        }

        if(segm == null){
        // the simple name is not in the scope. try it with the full name. (this should run in case of control-local instances)
        // TODO hack. the whole memory layout should be revised
           segm = global.lookupSegmentByName(getParentDecl().getName() + "." + getFirstFieldName());
        }

        if(segm == null){
        // the full name is not in the scope. maybe this is in an action body and refers to a control-local instance
            if(getParentControlDecl() != null)
                segm = global.lookupSegmentByName(getParentControlDecl().getName() + "." + getFirstFieldName());
        }

        if(segm == null){

            System.out.println(global);
            System.out.println(local);
            System.out.println(this);

            if(getParentControlDecl() != null){
                throw new IllegalStateException(
                    String.format(
                        "Neither %s or %s or %s were found in local nor global memory layout.", 
                        this.getFirstFieldName(),
                        getParentDecl().getName() + "." + getFirstFieldName(),
                        getParentControlDecl().getName() + "." + getFirstFieldName()));
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Neither %s or %s were found in local nor global memory layout.", 
                        this.getFirstFieldName(),
                        getParentDecl().getName() + "." + getFirstFieldName()));
            }
        }
        return segm;
    }

}
