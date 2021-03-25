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
package p4query.experts.callgraph;

import p4query.ontology.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;


public class CallGraphGeneratorTest {

    @Test
    public void testWhoCallsTable() {    
        GraphTraversalSource g = Befores.preTestWhoCallsTable();
        CallGraphGenerator.whoCallsTable(g);
        
        assertEquals(3, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
        assertEquals("4", g.V().has("nodeId", 1).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().values("nodeId").next().toString());
        assertEquals("5", g.V().has("nodeId", 2).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(0).values("nodeId").next().toString());
        assertEquals("6", g.V().has("nodeId", 2).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(1).values("nodeId").next().toString());
    }

    @Test
    public void testWhoCallsAction() {    
        GraphTraversalSource g = Befores.preTestWhoCallsAction();
        CallGraphGenerator.whoCallsAction(g);
        
        assertEquals(3, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
        assertEquals("4", g.V().has("nodeId", 1).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().values("nodeId").next().toString());
        assertEquals("5", g.V().has("nodeId", 2).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(0).values("nodeId").next().toString());
        assertEquals("6", g.V().has("nodeId", 2).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(1).values("nodeId").next().toString());
    }

    @Test
    public void testWhoCallsFunctionPrototype() {    
    GraphTraversalSource g = Befores.pretestWhoCallsFunctionPrototype();    
    CallGraphGenerator.whoCallsFunctionPrototype(g);
    
    assertEquals(5, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
    assertEquals("5", g.V().has("nodeId", 7).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().values("nodeId").next().toString());
    assertEquals("5", g.V().has("nodeId", 8).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(0).values("nodeId").next().toString());
    assertEquals("10", g.V().has("nodeId", 8).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(1).values("nodeId").next().toString());
    assertEquals("5", g.V().has("nodeId", 9).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(0).values("nodeId").next().toString());
    assertEquals("10", g.V().has("nodeId", 9).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(1).values("nodeId").next().toString());
    } 
    
    // todo get(0)/get(1) -> toorder? or better?
    @Test
    public void testWhoCallsParserState() {    
    GraphTraversalSource g = Befores.pretestWhoCallsParserState();    
    CallGraphGenerator.whoCallsParserState(g);
    
    assertEquals(3, g.E().has(Dom.Call.ROLE, Dom.Call.Role.CALLS).count().next().intValue());
    assertEquals("3", g.V().has("nodeId", 4).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().values("nodeId").next().toString());
    assertEquals("3", g.V().has("nodeId", 6).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(0).values("nodeId").next().toString());
    assertEquals("5", g.V().has("nodeId", 6).outE(Dom.CALL).has(Dom.Call.ROLE, Dom.Call.Role.CALLS).inV().fold().next().get(1).values("nodeId").next().toString());
    }
}