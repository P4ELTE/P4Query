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
package p4query.applications.smc.lir.typing;

public class UnresolvedVertexLabel extends Label {

    private Object srcVertex;

    public UnresolvedVertexLabel(Object srcVertex, String comment) {
        super(-1, comment);
        this.srcVertex = srcVertex;
    }

    public Object getVertex(){
        return srcVertex;
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
