package p4analyser.applications;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONVersion;
import org.apache.tinkerpop.shaded.jackson.core.JsonProcessingException;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.codejargon.feather.Provides;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import p4analyser.ontology.providers.Application;
import p4analyser.ontology.providers.AppUI;
import p4analyser.ontology.providers.P4FileProvider.InputP4File;
import p4analyser.ontology.analyses.SyntaxTree;
import p4analyser.ontology.Dom;
import p4analyser.ontology.Status;

public class App implements Application {

    private final TZTaskAppUI ui = new TZTaskAppUI();

    @Override
    public AppUI getUI() {
        return ui;
    }

    @Inject
    private GraphTraversalSource g;

    @Inject
    @InputP4File
    private File file;

    @Inject
    @SyntaxTree
    private Provider<Status> SyntaxTree;

    @Override
    public Status run() {
        if (ui.synTree)
            SyntaxTree.get();

        System.out.println("TZTask started!");

        System.out.println(analyse(g, "ipv4", "dstAddr"));

        System.out.println("TZTask DONE!");

        return new Status();
    }

    public static  Map<Object, Object> analyse(GraphTraversalSource g, String header, String field) {
        Map<Object, Object> query = g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext").group()
                .by(__.values(Dom.Syn.V.NODE_ID))
                .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV().repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(__.values(Dom.Syn.V.VALUE).is(P.neq("."))).values(Dom.Syn.V.NODE_ID, Dom.Syn.V.VALUE)
                        .fold())
                .next();

        List<Object> keyElementContextNodeIds = new ArrayList<Object>();

        for (Object nodeId : query.keySet()) {
            List<Object> valuesList = (List<Object>) query.get(nodeId);
            Map<String, Long> map = new HashMap<String, Long>();

            for (int i = 0; i < valuesList.size(); i += 2) {
                map.put((String) valuesList.get(i), (Long) valuesList.get(i + 1));
            }

            if (map.size() == 3 && map.containsKey("hdr") && map.containsKey(header) && map.containsKey(field)
                    && map.get("hdr") < map.get(header) && map.get(header) < map.get(field)) {
                keyElementContextNodeIds.add(nodeId);
            }
        }

        ObjectMapper mapper = GraphSONMapper.build().version(GraphSONVersion.V1_0).create().createMapper();

        ObjectMapper mapperJson = new ObjectMapper();
        //Map<String,Object> mapperJson = mapperJson.readValue(json, Map.class);

        /*return 
            mapper.writeValueAsString((g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext")
                .filter(__.values(Dom.Syn.V.NODE_ID).is(P.within(keyElementContextNodeIds)))
                //.project("TableName", "LookUp", "Keys")
                .group()
                .by(__.repeat(__.in(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TableDeclarationContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .values(Dom.Syn.V.VALUE)
                )

                .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .values(Dom.Syn.V.VALUE)
                )

                .by(__.inE(Dom.SYN).outV().inE(Dom.SYN).outV().inE(Dom.SYN).outV()
                    .outE(Dom.SYN).inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))

                    .project("KeyElementContextNodeId", "Lista")

                    .by(
                        __.values(Dom.Syn.V.NODE_ID)
                    )
                    .group()
                    .by(__.values(Dom.Syn.V.NODE_ID))
                    .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()

                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(__.values(Dom.Syn.V.VALUE).is(P.neq(".")))
                        .order().by(Dom.Syn.V.NODE_ID)
                        .values(Dom.Syn.V.VALUE)
                        .fold()
                    )          
                )
            ).next());*/



        /*return
            mapper.writeValueAsString((g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext")
                .filter(__.values(Dom.Syn.V.NODE_ID).is(P.within(keyElementContextNodeIds)))
                .project("TablaNev", "LookUp", "Ertekek")
                .by(__.repeat(__.in(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TableDeclarationContext"))
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .values(Dom.Syn.V.VALUE))

                .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .values(Dom.Syn.V.VALUE))

                .by(__.inE(Dom.SYN).outV().inE(Dom.SYN).outV().inE(Dom.SYN).outV()
                    .outE(Dom.SYN).inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()

                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .filter(__.values(Dom.Syn.V.VALUE).is(P.neq(".")))
                    .values(Dom.Syn.V.VALUE)
                    .fold()
                )
            ).next());*/


        return
            (g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "KeyElementContext")
                .filter(__.values(Dom.Syn.V.NODE_ID).is(P.within(keyElementContextNodeIds)))
                .group()
                .by(__.group()
                    .by(__.repeat(__.in(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TableDeclarationContext"))
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .values(Dom.Syn.V.VALUE))

                    .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .values(Dom.Syn.V.VALUE)))

                .by(__.inE(Dom.SYN).outV().inE(Dom.SYN).outV().inE(Dom.SYN).outV()
                    .outE(Dom.SYN).inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "KeyElementContext"))

                    .group()
                    .by(__.values(Dom.Syn.V.NODE_ID))
                    .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()

                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .filter(__.values(Dom.Syn.V.VALUE).is(P.neq(".")))
                        .order().by(Dom.Syn.V.NODE_ID)
                        .values(Dom.Syn.V.VALUE)
                        .fold()
                    )       
                )
            ).next();
    }
}
