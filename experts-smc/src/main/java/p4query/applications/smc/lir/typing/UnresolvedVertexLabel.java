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
package p4query.applications.smc.lir.typing;

import p4query.applications.smc.hir.p4api.Declaration;

public class UnresolvedVertexLabel extends Label {

    private Object srcVertex;
    private Declaration parent;

    public UnresolvedVertexLabel(Object srcVertex, String comment, Declaration parent) {
        super(-1, comment);
        this.srcVertex = srcVertex;
        this.parent = parent;
    }

    public Object getVertex(){
        return srcVertex;
    }

    public Declaration getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return String.format("UnresolvedVertexLabel[%s, '%s']", srcVertex, comment);
    }
    
    @Override
    public String toHumanReadable() {
        return integer + ": unresolved label to " + comment;
    }
}
