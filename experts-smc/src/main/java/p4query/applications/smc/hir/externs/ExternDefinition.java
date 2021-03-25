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
package p4query.applications.smc.hir.externs;

import java.util.List;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;
import p4query.applications.smc.lir.iset.StackInstruction;

public abstract class ExternDefinition implements Definition {
    protected String name;
    protected String namespace;
    protected LocalStruct local;

    private ExternDefinition(){}

    protected ExternDefinition(String name, String namespace){
        this.name = name;
        this.namespace = namespace;
        this.local = new LocalStruct(this);

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
    public LocalStruct getLocal() {
        return this.local;
    }

    @Override
    public String addTemporary(IRType type) {
        return local.addTemporary(type);
    }
    
}
