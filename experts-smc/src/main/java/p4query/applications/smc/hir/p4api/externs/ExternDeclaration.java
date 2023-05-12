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
package p4query.applications.smc.hir.p4api.externs;

import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.LocalStruct;

public abstract class ExternDeclaration extends Declaration {
    protected String name;
    protected String namespace;
    protected LocalStruct local;
    protected LocalStruct temps;
    private LocalStruct controlLocals;
    private LocalStruct locals;

    protected ExternDeclaration(String name, String namespace){
        this.name = name;
        this.namespace = namespace;
        this.local = new LocalStruct(this);
        this.temps = new LocalStruct(this);
        this.controlLocals = new LocalStruct(this); // this will stay empty
        this.locals = new LocalStruct(this); // this will stay empty
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
    public LocalStruct getParameters() {
        return this.local;
    }

    @Override
    public LocalStruct getControlLocals() {
        return this.controlLocals;
    }

    @Override
    public LocalStruct getLocals() {
        return this.locals;
    }

    @Override
    public String addTemporary(IRType type) {
        return temps.addTemporary(type, type.getName());
    }
    
    @Override
    public LocalStruct getTemps() {
        return temps;
    }
}
