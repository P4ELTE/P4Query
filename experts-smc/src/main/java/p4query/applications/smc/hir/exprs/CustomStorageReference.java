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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.typing.IRType;

public class CustomStorageReference extends StorageReference {

    private String name;
    private Expression origin;
    private IRType type;
    private Declaration parentDecl;
    private Declaration parentControlDecl;

    public CustomStorageReference(CompilerState state, String vClass, String name, IRType type, Expression origin) {
        this.name = name;
        this.type = type;
        this.origin = origin;
        this.parentDecl = state.getParentDecl();
        this.parentControlDecl = findParentControlDecl(state);
    }

    @Override
    public String toString() {
        return "CustomStorageReference [name=" + name + ", type=" + type + "]";
    }

    @Override
    public String getFirstFieldName() {
        return name;
    }

    @Override
    public int getSizeOffset() {
        return 0;
    }

    @Override
    public IRType getTypeHint() {
        return type;
    }

    @Override
    public String toP4Syntax() {
        return name;
    }

    @Override
    public String getTailFields() {
        return "";
    }

    @Override
    public StorageReference getStorageReference() {
        return this;
    }

    @Override
    protected List<String> getFieldList() {
        return Arrays.asList(name);
    }

    @Override
    protected LinkedHashMap<String, IRType> getFields() {
        LinkedHashMap<String, IRType> m = new LinkedHashMap<>();
        m.put(name, type);
        return m;
    }

    @Override
    public Declaration getParentDecl() {
        return parentDecl;
    }

    @Override
    public Declaration getParentControlDecl() {
        return parentControlDecl;
    }


    
}
