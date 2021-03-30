package p4query.applications;

import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

import p4query.ontology.Dom;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    
    public static GraphTraversalSource preparation()
    {
        TinkerGraph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();
        
        Vertex parserDecCon = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "ParserDeclarationContext").property("nodeId", 0).next();
        Vertex tniParser = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 2).property(Dom.Syn.V.VALUE, "MyParser").next();
        Vertex parserStateCon = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "ParserStateContext").property("nodeId", 3).next();
        Vertex tniState = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 4).property(Dom.Syn.V.VALUE, "parse_ethernet").next();
        Vertex selectExprCon = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "SelectExpressionContext").property("nodeId", 5).next();
        Vertex selectCaseCon = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "SelectCaseContext").property("nodeId", 6).next();
        Vertex tniKey1 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 7).property(Dom.Syn.V.VALUE, "default").next();
        Vertex tniKey2 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 8).property(Dom.Syn.V.VALUE, "TYPE_IPV4").next();
        Vertex tniHeader1 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 9).property(Dom.Syn.V.VALUE, "hdr").next();
        Vertex tniHeader2 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 10).property(Dom.Syn.V.VALUE, "ethernet").next();
        Vertex tniHeader3 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl").property("nodeId", 11).property(Dom.Syn.V.VALUE, "etherType").next();
                
        Vertex rand1 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 12).next();
        Vertex rand2 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 13).next();
        Vertex rand3 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 14).next();
        Vertex rand4 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 15).next();
        Vertex rand5 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 16).next();
        Vertex rand6 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 17).next();        
        Vertex rand7 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 18).next();
        Vertex rand8 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 19).next();
        Vertex rand9 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 20).next();
        Vertex rand10 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 21).next();
        Vertex rand11 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp").property("nodeId", 22).next();


        g.addE(Dom.SYN).property(Dom.Syn.E.RULE, "parserTypeDeclaration").from(parserDecCon).to(rand1)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "name").from(rand1).to(rand2)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand2).to(tniParser)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "parserState").from(parserDecCon).to(parserStateCon)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "name").from(parserStateCon).to(rand3)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand3).to(tniState)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(parserStateCon).to(rand4)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "selectExpression").from(rand4).to(selectExprCon)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "selectCaseList").from(selectExprCon).to(rand5)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand5).to(selectCaseCon)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "keysetExpression").from(selectCaseCon).to(rand6)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand6).to(rand8)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand8).to(tniKey1)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand8).to(tniKey2)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "expressionList").from(selectExprCon).to(rand7)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand7).to(rand9)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand7).to(rand10)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand7).to(rand11)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand9).to(tniHeader1)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand10).to(tniHeader2)
        .addE(Dom.SYN).property(Dom.Syn.E.RULE, "tmp").from(rand11).to(tniHeader3);

        return g;
    }

    @Test
    public void analyseTest()
    {
        GraphTraversalSource g = preparation();
        Object result = App.an(g, "ethernet", "etherType");

        List<Object> a = g.V().has(Dom.Syn.V.CLASS, "TerminalNodeImpl").values(Dom.Syn.V.VALUE).toList();

        //List<Object> b = g.V().has(Dom.Syn.V.CLASS, "SelectExpressionContext").values(Dom.Syn.V.NODE_ID).toList();

        System.out.println(a);
        System.out.println(result);
    }
}
