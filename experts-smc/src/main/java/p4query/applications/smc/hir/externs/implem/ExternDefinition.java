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

import p4query.applications.smc.hir.externs.UnableToLinkDeclaration;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;

public abstract class ExternDefinition extends Definition {

    private String expectedNamespace;
    private String expectedName;

    protected ExternDefinition(Declaration iface, String name, String namespace) throws UnableToLinkDeclaration {
        this.expectedName = name;
        this.expectedNamespace = namespace;

        matchDeclaration(iface); // throws exception
        setDeclaration(iface);
    }

    public String expectedName() {
        return expectedName;
    }

    public String expectedNamespace() {
        return expectedNamespace;
    }

    private void matchDeclaration(Declaration decl) throws UnableToLinkDeclaration{
        if(!decl.getName().equals(expectedName())) 
            throw new UnableToLinkDeclaration(decl, this);
        if(decl.getNamespace() == null && expectedNamespace() == null) 
            return;
        if(decl.getNamespace() == null) 
            throw new UnableToLinkDeclaration(decl, this);
        if(expectedNamespace() == null) 
            throw new UnableToLinkDeclaration(decl, this);
        if(!decl.getNamespace() .equals(expectedNamespace())) 
            throw new UnableToLinkDeclaration(decl, this);
    }


}
