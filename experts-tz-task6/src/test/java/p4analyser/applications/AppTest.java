package p4analyser.applications;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.shaded.jackson.core.JsonProcessingException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.junit.Test;

import p4analyser.ontology.Dom;

public class AppTest {
    public static GraphTraversalSource preparation() {
        TinkerGraph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();

        Long counter = (long) 0;
        Vertex tableDeclarationContext = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TableDeclarationContext")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex tempForNameTableDeclarationContext = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex tableDeclarationContextName = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property("nodeId", counter).property(Dom.Syn.V.VALUE, "ipv4_lpm").next();
        ++counter;
        Vertex keyElementContextAndTableDeclarationContextTemp1 = g.addV(Dom.SYN)
                        .property(Dom.Syn.V.CLASS, "tableDeclarationContext").property("nodeId", counter)
                        .next();
        ++counter;
        Vertex keyElementContextAndTableDeclarationContextTemp2 = g.addV(Dom.SYN)
                        .property(Dom.Syn.V.CLASS, "tableDeclarationContext").property("nodeId", counter)
                        .next();
        ++counter;
        Vertex keyElementContextAndTableDeclarationContextTemp3 = g.addV(Dom.SYN)
                        .property(Dom.Syn.V.CLASS, "tableDeclarationContext").property("nodeId", counter)
                        .next();
        ++counter;
        Vertex keyElementContextAndTableDeclarationContextTemp4 = g.addV(Dom.SYN)
                        .property(Dom.Syn.V.CLASS, "tableDeclarationContext").property("nodeId", counter)
                        .next();
        ++counter;
        Vertex keyElementContext = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "KeyElementContext")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex tempForKeyElementContext = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex hdr = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property(Dom.Syn.V.VALUE, "hdr").property("nodeId", counter).next();
        ++counter;
        Vertex ipv4 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property(Dom.Syn.V.VALUE, "ipv4").property("nodeId", counter).next();
        ++counter;
        Vertex dstAddr = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property(Dom.Syn.V.VALUE, "dstAddr").property("nodeId", counter).next();
        ++counter;
        Vertex tempForKeyElementContextName = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex lookUpName = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property("nodeId", counter).property(Dom.Syn.V.VALUE, "lpm").next();

        ++counter;
        Vertex keyElementContext2 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "KeyElementContext")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex tempForKeyElementContext21 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex hdr1 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property(Dom.Syn.V.VALUE, "hdr1").property("nodeId", counter).next();
        ++counter;
        Vertex ipv42 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property(Dom.Syn.V.VALUE, "ipv42").property("nodeId", counter).next();
        ++counter;
        Vertex dstAddr3 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property(Dom.Syn.V.VALUE, "dstAddr3").property("nodeId", counter).next();
        ++counter;
        Vertex tempForKeyElementContextName2 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "tmp")
                        .property("nodeId", counter).next();
        ++counter;
        Vertex lookUpName2 = g.addV(Dom.SYN).property(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                        .property("nodeId", counter).property(Dom.Syn.V.VALUE, "LookUpName2").next();

        g.addE(Dom.SYN).property(Dom.Syn.E.RULE, "name").from(tableDeclarationContext)
            .to(tempForNameTableDeclarationContext).addE(Dom.SYN)
            .from(tempForNameTableDeclarationContext).to(tableDeclarationContextName).addE(Dom.SYN)
            .from(tableDeclarationContext).to(keyElementContextAndTableDeclarationContextTemp1)
            .addE(Dom.SYN).from(keyElementContextAndTableDeclarationContextTemp1)
            .to(keyElementContextAndTableDeclarationContextTemp2).addE(Dom.SYN)
            .from(keyElementContextAndTableDeclarationContextTemp2)
            .to(keyElementContextAndTableDeclarationContextTemp3).addE(Dom.SYN)
            .from(keyElementContextAndTableDeclarationContextTemp3)
            .to(keyElementContextAndTableDeclarationContextTemp4).addE(Dom.SYN)
            .from(keyElementContextAndTableDeclarationContextTemp4).to(keyElementContext)
            .addE(Dom.SYN).property(Dom.Syn.E.RULE, "expression").from(keyElementContext)
            .to(tempForKeyElementContext).addE(Dom.SYN).from(tempForKeyElementContext).to(hdr)
            .addE(Dom.SYN).from(tempForKeyElementContext).to(ipv4).addE(Dom.SYN)
            .from(tempForKeyElementContext).to(dstAddr).addE(Dom.SYN)
            .property(Dom.Syn.E.RULE, "name").from(keyElementContext)
            .to(tempForKeyElementContextName).addE(Dom.SYN).from(tempForKeyElementContextName)
            .to(lookUpName)

            .addE(Dom.SYN).from(keyElementContextAndTableDeclarationContextTemp4)
            .to(keyElementContext2).addE(Dom.SYN).property(Dom.Syn.E.RULE, "expression")
            .from(keyElementContext2).to(tempForKeyElementContext21).addE(Dom.SYN)
            .from(tempForKeyElementContext21).to(hdr1).addE(Dom.SYN)
            .from(tempForKeyElementContext21).to(ipv42).addE(Dom.SYN)
            .from(tempForKeyElementContext21).to(dstAddr3).addE(Dom.SYN)
            .property(Dom.Syn.E.RULE, "name").from(keyElementContext2)
            .to(tempForKeyElementContextName2).addE(Dom.SYN).from(tempForKeyElementContextName2)
            .to(lookUpName2)

            .iterate();

        return g;
    }

    @Test
    public void shouldAnswerWithTrue()
    {
        GraphTraversalSource g = preparation();
        Map<Object, Object> result = App.analyse(g, "ipv4", "dstAddr");
        
        System.out.println(result);
        assertTrue( true );
    }
}
