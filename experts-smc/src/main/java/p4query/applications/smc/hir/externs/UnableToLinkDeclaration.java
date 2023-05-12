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
package p4query.applications.smc.hir.externs;

import p4query.applications.smc.hir.externs.implem.ExternDefinition;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;

public class UnableToLinkDeclaration extends Exception {
    public UnableToLinkDeclaration(Declaration decl, ExternDefinition def){
        super(
            String.format("Cannot link definition %s::%s to declaration %s::%s",
            def.expectedNamespace(),
            def.expectedName(),
            decl.getName(),
            decl.getNamespace()));
    }
    
}
