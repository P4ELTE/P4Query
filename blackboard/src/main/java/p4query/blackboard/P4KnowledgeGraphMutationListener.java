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
package p4query.blackboard;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.event.MutationListener;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.HashMap;
import java.util.Map;
// example MutationListener:
// import org.apache.tinkerpop.gremlin.process.traversal.step.util.event.ConsoleMutationListener;

// IMPORTANT: this is not thread-safe!
public class P4KnowledgeGraphMutationListener implements MutationListener {

    private Map<String, Long> m = new HashMap<>();

    private Graph graph;

    private P4KnowledgeGraphMutationListener(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void vertexAdded(Vertex vertex) {
        String label = vertex.label();
        Long n = m.get(label);
        if(n == null){
            n = 0L;
        }
        graph.traversal().V(vertex).property("nodeId", n).iterate();
        m.put(label, n+1);
    }

    @Override
    public void vertexRemoved(Vertex vertex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vertexPropertyChanged(Vertex element, VertexProperty oldValue, Object setValue,
            Object... vertexPropertyKeyValues) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vertexPropertyRemoved(VertexProperty vertexProperty) {
        // TODO Auto-generated method stub

    }

    @Override
    public void edgeAdded(Edge edge) {
        graph.traversal()
             .E(edge)
             .property(
                 "ord",  
                 __.<Edge>as("e").outV().outE()
                   .where(P.eq("e")).by(T.label)   // select edges that has the same label as 'e'
                   .count()
                   .map(t -> t.get() - 1))
             .iterate();

    }

    @Override
    public void edgeRemoved(Edge edge) {
        // TODO Auto-generated method stub

    }

    @Override
    public void edgePropertyChanged(Edge element, Property oldValue, Object setValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void edgePropertyRemoved(Edge element, Property property) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vertexPropertyPropertyChanged(VertexProperty element, Property oldValue, Object setValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vertexPropertyPropertyRemoved(VertexProperty element, Property property) {
        // TODO Auto-generated method stub

    }


}
