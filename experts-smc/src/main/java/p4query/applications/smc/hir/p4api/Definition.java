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
package p4query.applications.smc.hir.p4api;

import java.util.List;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.StackInstruction;

public abstract class Definition {

    private Declaration iface;

    protected Definition() {
    }

    public void setDeclaration(Declaration iface){

        if(this.iface != null && this.iface != iface){
            throw new IllegalStateException(
                String.format(
                    "Multiple declarations found for definition %s: [%s, %s].", 
                    this, 
                    this.iface, 
                    iface));
        }

        this.iface = iface;
    }
    public Declaration getDeclaration(){
        return iface;
    }

    abstract public List<StackInstruction> compileToLIR(GlobalMemoryLayout global);

    abstract public void init(CompilerState state);

    protected Comment openDefinition(){
        return openDefinition("");
    }
    protected Comment openDefinition(String parameterList){
        return new Comment(
            String.format("definition of %s(%s)", getDeclaration().getName(), parameterList));
    }
    protected Comment closeDefinition(){
        return closeDefinition("");
    }
    protected Comment closeDefinition(String parameterList){
        return new Comment(
            String.format("end of definition of %s(%s)%n//%n", getDeclaration().getName(), parameterList));
    }

    @Override
    public String toString() {
//        return String.format("%s::%s/%s", getNamespace(), getName(), getLocal().getFields().size());
        return String.format("Definition[ns=%s, name=%s, arity=%s, fields=%s]", 
            getDeclaration().getNamespace(),
            getDeclaration().getName(), 
            getDeclaration().getParameters().getFields().size(),  
            getDeclaration().getParameters().getFields().keySet());
    }
}
