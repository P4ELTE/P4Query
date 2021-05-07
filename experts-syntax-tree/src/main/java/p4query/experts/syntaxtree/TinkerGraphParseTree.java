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
package p4query.experts.syntaxtree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.experts.syntaxtree.p4.P4BaseListener;
import p4query.ontology.Dom;

public class TinkerGraphParseTree {
    private final static int BATCH_SIZE = 50;

    public static void fromParseTree(GraphTraversalSource g, ParseTree tree, Vocabulary vocab, String[] ruleNames) {
        Result r = new Result();
        new ParseTreeWalker().walk(new GraphCreatorListener(g, vocab, ruleNames, r), tree);

        r.upload(g);
    }

    static class GraphCreatorListener extends P4BaseListener {
        private GraphTraversalSource g;
        private Map<ParseTree, VertexResult> ids = new HashMap<>();
        private Vocabulary vocab;
        private String[] ruleNames;
        private Result result;
        public GraphCreatorListener(GraphTraversalSource g, Vocabulary vocab, String[] ruleNames, Result r) {
            this.g = g;
            this.vocab = vocab;
            this.ruleNames = ruleNames;
            this.result = r;
        }

        // TODO this is output specific. Move to GraphUtils
        private static String sanitize(String str){
            str = str.replaceAll("<", "\\\\<");
            str = str.replaceAll(">", "\\\\>");
            str = str.replaceAll("\\{", "\\\\{");
            str = str.replaceAll("\\}", "\\\\}");
            str = str.replaceAll("\"", "\\\\\"");
            return str;
        }

        public void visitTerminal(TerminalNode node){
            VertexResult vr = new VertexResult(Dom.SYN);
            vr.addProp(Dom.Syn.V.CLASS, node.getClass().getSimpleName());
            vr.addProp(Dom.Syn.V.START, node.getSymbol().getStartIndex());
            vr.addProp(Dom.Syn.V.END, node.getSymbol().getStopIndex());
            vr.addProp(Dom.Syn.V.LINE, node.getSymbol().getLine());
            vr.addProp(Dom.Syn.V.VALUE, sanitize(node.getText()));

            ids.put(node, vr);

            if(node.getParent() == null) return;
            Object parentId = ids.get(node.getParent());
            if(parentId == null) throw new RuntimeException("parentId == null");

            EdgeResult er = new EdgeResult(Dom.SYN, ids.get(node.getParent()), vr);
            er.addProp(Dom.Syn.E.RULE, vocab.getSymbolicName(node.getSymbol().getType()));

            result.verts.add(vr);
            result.edges.add(er);
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx){
            // note: getStop is null, if the rule maps to the empty string. (e.g.  input : /* epsilon */)
            int stopIdx = ctx.getStop() == null 
                          ? ctx.getStart().getStopIndex() 
                          : ctx.getStop().getStopIndex();
            
            VertexResult vr = new VertexResult(Dom.SYN);
            vr.addProp(Dom.Syn.V.CLASS, ctx.getClass().getSimpleName());
            vr.addProp(Dom.Syn.V.START, ctx.getStart().getStartIndex());
            vr.addProp(Dom.Syn.V.END, stopIdx);
            vr.addProp(Dom.Syn.V.LINE, ctx.getStart().getLine());

            ids.put(ctx, vr);

// // note: this is not used since ORD is now set on the server-side
//            if(ctx.parent == null) return;
//            Object parentId = ids.get(ctx.parent);
//            int childIdx = -1;
//            for (int i = 0; i < ctx.parent.getChildCount(); i++) {
//               if(!ctx.parent.getChild(i).equals(ctx)) continue;
//               childIdx = i;
//               break;
//            }
//
//            if(parentId == null) throw new RuntimeException("parentId == null");

            EdgeResult er = new EdgeResult(Dom.SYN, ids.get(ctx.parent), vr);
            er.addProp(Dom.Syn.E.RULE, ruleNames[ctx.getRuleIndex()]);

            result.verts.add(vr);
            result.edges.add(er);
        }

    }

    // these are used for batching
    private static class Result {
        LinkedList<VertexResult> verts = new LinkedList<>();
        LinkedList<EdgeResult> edges = new LinkedList<>();

        public void upload(GraphTraversalSource g){

            long startTime = System.currentTimeMillis();

            int batchCount = (int) Math.ceil(verts.size() / (double) BATCH_SIZE);
            Map<VertexResult, Vertex> vertMap = uploadVertices(g, BATCH_SIZE, batchCount);
            uploadEdges(g, BATCH_SIZE, batchCount, vertMap); 

            long stopTime = System.currentTimeMillis();
            System.out.println(String.format("Graph uploading took %s ms.", stopTime - startTime));
        }

        private Map<VertexResult, Vertex> uploadVertices(GraphTraversalSource g, int batchSize, int batchCount) {

            Iterator<VertexResult> vrs = verts.iterator();
            for (int i = 0; i < batchCount; i++) {

                // note: it's inconvenient to add the first vertex separately, but for some reason the gdb only added the vertices if I created the traversal using g.addV().

                VertexResult vr = vrs.next();
                GraphTraversal<Vertex, Vertex> t = g.addV(vr.label);
                for (Map.Entry<String, String> e : vr.props.entrySet()) {
                   t = t.property(e.getKey(), e.getValue()); 
                }

                for (int j = 1; j < batchSize && vrs.hasNext(); j++) {
                    vr = vrs.next();
                    t = t.addV(vr.label);
                    for (Map.Entry<String, String> e : vr.props.entrySet()) {
                        t = t.property(e.getKey(), e.getValue()); 
                    }
                }
                t.iterate();
            }

            HashMap<VertexResult, Vertex> vrvs = new HashMap<>();
            // note: sorting by nodeId tells us the insertion order
            vrs = verts.iterator();
            List<Vertex> vs = g.V().order().by(Dom.Syn.V.NODE_ID).toList();
            for (Vertex v : vs) {
               vrvs.put(vrs.next(), v); 
            }
            return vrvs;
        }

        private void uploadEdges(GraphTraversalSource g, int batchSize, int batchCount, Map<VertexResult, Vertex> vertMap) {

            Iterator<EdgeResult> ers = edges.iterator();
            ers.next(); // ignore the root node, it has no incoming edge;

            for (int i = 0; i < batchCount; i++) {
                EdgeResult er = ers.next();
                GraphTraversal<Edge, Edge> t = g.addE(er.label);
                t = t.from(vertMap.get(er.source));
                t = t.to(vertMap.get(er.dest));
                for (Map.Entry<String, String> e : er.props.entrySet()) {
                   t = t.property(e.getKey(), e.getValue()); 
                }

                for (int j = 1; j < batchSize && ers.hasNext(); j++) {
                    er = ers.next();
                    t = t.addE(er.label);
                    t = t.from(vertMap.get(er.source));
                    t = t.to(vertMap.get(er.dest));
                    for (Map.Entry<String, String> e : er.props.entrySet()) {
                        t = t.property(e.getKey(), e.getValue()); 
                    }
                }
                t.iterate();
            }

        }
    }

    private static class VertexResult {
        public String label;
        public LinkedHashMap<String, String> props = new LinkedHashMap<>();

        public VertexResult(String label) {
            this.label = label;
        }

        public void addProp(String key, Integer value){
            props.put(key, value.toString());
        }
        public void addProp(String key, String value){
            props.put(key, value);
        }
    }
    private static class EdgeResult {
        public String label;
        public VertexResult source;
        public VertexResult dest;
        public LinkedHashMap<String, String> props = new LinkedHashMap<>();


        public EdgeResult(String label, VertexResult source, VertexResult dest) {
            this.label = label;
            this.source = source;
            this.dest = dest;
        }

        public void addProp(String key, String value){
            props.put(key, value);
        }

    }

}
    