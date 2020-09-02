package parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.TraversalOptionParent.Pick;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;

public class SemanticAnalysis {
    
    public static void analyse(Graph graph){
        GraphTraversalSource g = graph.traversal();
        Parser.analyse(g);
        Control.analyse(g);
        Instantiation.analyse(g);

        Symbol.analyse(g);
    }

    private static class Parser {
        private static void analyse(GraphTraversalSource g) {
            findParsers(g);
            findParserNames(g);
            findStates(g);
            findStateNames(g);
            findTransitions(g);
        }

        private static void findParsers(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ParserDeclarationContext")
            .addE(Dom.SEM).from(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, 0))
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).property(Dom.Sem.ROLE, Dom.Sem.Role.Top.PARSER)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();
        }

        private static void findParserNames(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.PARSER).inV()
                .as("parserRoot")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserTypeDeclaration").inV()
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .addE(Dom.SEM).from("parserRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }


        // TODO until -> emit
        private static void findStates(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.PARSER).inV()
                .as("parserRoot")
                .repeat(__.out(Dom.SYN))
                .until(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserStates").count().is(0))
                .emit(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserState"))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserState").inV()
                .addE(Dom.SEM).from("parserRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        private static void findStateNames(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE).inV()
            .as("stateRoot")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name")
            .inV()
            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .addE(Dom.SEM).from("stateRoot")
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();
        }

        private static void findTransitions(GraphTraversalSource g) {
            findTransitionNode(g);
            findTransitionTargetName(g);
            findTransitionSelectCase(g);
            findTransitionSelectHead(g);
            findTransitionSelectCaseName(g);
            findStartState(g);
            findNextState(g);
            findStatements(g);

        }


        private static void findTransitionSelectCaseName(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.CASE).inV()
                .as("caseRoot")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .addE(Dom.SEM).from("caseRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER)
                .property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        private static void findTransitionSelectHead(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.TRANSITION).inV()
                .as("transitionRoot")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "selectExpression").inV() 
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV() 
                .addE(Dom.SEM)
                .from("transitionRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.HEAD)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        // TODO until -> emit
        private static void findTransitionSelectCase(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.TRANSITION).inV()
                .as("transitionRoot")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "selectExpression").inV() 
                .repeat(__.out(Dom.SYN))
                .until(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "selectCaseList").count().is(0))
                .emit(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "selectCase"))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "selectCase").inV()
                .addE(Dom.SEM).from("transitionRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.CASE)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        private static void findTransitionTargetName(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.TRANSITION).inV()
                .as("transitionRoot")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                .repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .addE(Dom.SEM).from("transitionRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        private static void findTransitionNode(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE).inV()
            .as("stateRoot")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "transitionStatement").inV()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "stateExpression").inV()
            .addE(Dom.SEM).from("stateRoot")
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.TRANSITION)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();
        }

        private static void findStartState(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.PARSER).inV()
            .as("parserRoot")
            .outE(Dom.SEM).property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE).inV()
            .filter(__.outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME).inV()
                      .has(Dom.Syn.V.VALUE, "start"))
            .addE(Dom.SEM).from("parserRoot")
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.START)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();
        }

        @SuppressWarnings("unchecked")
        private static void findNextState(GraphTraversalSource g) {
            List<Map<String,Vertex>> statesAndNextNames = 
                g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE).inV()
                .as("sourceState")
                .outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.TRANSITION).inV()
                .union(
                    __.outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE,Dom.Sem.Role.Parser.NAME).inV(),
                    __.outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE,Dom.Sem.Role.Parser.CASE).inV()
                      .outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE,Dom.Sem.Role.Parser.NAME).inV())
                .as("nextName")
                .<Vertex>select("sourceState", "nextName")
                .toList();

            for (Map<String,Vertex> sn : statesAndNextNames) {
                Vertex state = sn.get("sourceState");
                String nextName = sn.get("nextName").value(Dom.Syn.V.VALUE);

                if(nextName.equals("accept") || nextName.equals("reject")){

                    g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.PARSER).inV()
                    .addE(Dom.SEM).to(__.V(state))
                    .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.FINAL)
                    .sideEffect(GremlinUtils.setEdgeOrd())
                    .iterate();

                } else {

                    g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE).inV()
                    .filter(__.outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME).inV()
                                .sideEffect(t -> t.get().value(Dom.Syn.V.CLASS))
                            .has(Dom.Syn.V.VALUE, nextName))
                    .addE(Dom.SEM).from(__.V(state))
                    .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NEXT)
                    .sideEffect(GremlinUtils.setEdgeOrd())
                    .iterate();
                }
            }
        }

        // TODO until -> emit
        private static void findStatements(GraphTraversalSource g){

            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATE).inV()
            .as("synState")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserStatements").inV()
            .repeat(__.out())
            .until(__.has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").or()
                    .has(Dom.Syn.V.CLASS, "DirectApplicationContext").or()
                    .has(Dom.Syn.V.CLASS, "ConstantDeclarationContext").or()
                    .has(Dom.Syn.V.CLASS, "VariableDeclarationContext"))
            .addE(Dom.SEM).from("synState")
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.PARSER).property(Dom.Sem.ROLE, Dom.Sem.Role.Parser.STATEMENT)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();

        }
    }

    private static class Control {
        private static void analyse(GraphTraversalSource g) {
            findControl(g);
            findControlName(g);
            findControlBody(g);
            findBlockStatements(g);
            findConditionalBranches(g);
            findLastStatements(g);
            findReturnStatements(g);
        }

        // NOTE this is almost equivalent to the parser
        private static void findControl(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ControlDeclarationContext")
            .addE(Dom.SEM).from(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, 0))
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).property(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();
        }

        // NOTE this is almost equivalent to the parser
        private static void findControlName(GraphTraversalSource g) {

            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL).inV()
                .as("controlRoot")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlTypeDeclaration").inV()
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .addE(Dom.SEM).from("controlRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL).property(Dom.Sem.ROLE, Dom.Sem.Role.Control.NAME)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        private static void findControlBody(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM)
             .has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL).inV()
             .addE(Dom.SEM).to(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlBody").inV().out(Dom.SYN))
             .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL).property(Dom.Sem.ROLE, Dom.Sem.Role.Control.BODY)
             .sideEffect(GremlinUtils.setEdgeOrd())
             .iterate();
        }

        private static void findBlockStatements(GraphTraversalSource g) {
        // Note: 
        // - The syntax tree has represents linked lists in reverse-order: the head is the leaf.
        // - Gremlin has no reverse operation. It can be simulated using fold() and Collections.reverse, but then path information (incl. names) is lost.
            List<Map<String, Vertex>> ms = 
                g.E().hasLabel(Dom.SEM)
                .has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL).inV()
                .repeat(__.out(Dom.SYN))
                .emit(__.has(Dom.Syn.V.CLASS, "BlockStatementContext"))
                .as("blockRoot")
                .repeat(__.out())
                .until(__.has(Dom.Syn.V.CLASS, "AssignmentOrMethodCallStatementContext").or()
                        .has(Dom.Syn.V.CLASS, "DirectApplicationContext").or()
                        .has(Dom.Syn.V.CLASS, "ConditionalStatementContext").or()
                        .has(Dom.Syn.V.CLASS, "BlockStatementContext").or()
                        .has(Dom.Syn.V.CLASS, "EmptyStatement").or()
                        .has(Dom.Syn.V.CLASS, "ExitStatement").or()
                        .has(Dom.Syn.V.CLASS, "ReturnStatement").or()
                        .has(Dom.Syn.V.CLASS, "SwitchStatement"))
                .as("statement")
                .<Vertex>select("blockRoot", "statement")
                .toList();

            Collections.reverse(ms);
            for (Map<String,Vertex> m : ms) {
                Vertex blockRoot = m.get("blockRoot");
                Vertex statement = m.get("statement");
                
                g.V(statement).choose(__.values(Dom.Syn.V.CLASS))
                .option("BlockStatementContext",
                    __.addE(Dom.SEM).from(__.V(blockRoot))
                    .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST))
                .option("ConditionalStatementContext",
                    __.addE(Dom.SEM).from(__.V(blockRoot))
                    .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST))
                .option(Pick.none,
                    __.addE(Dom.SEM).from(__.V(blockRoot))
                    .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.STATEMENT))
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
            }
        }


        @SuppressWarnings("unchecked")
        private static void findConditionalBranches(GraphTraversalSource g) {

            g.E().hasLabel(Dom.SEM)
             .has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL).inV()
             .repeat(__.out(Dom.SYN))
             .emit(__.has(Dom.Syn.V.CLASS, "ConditionalStatementContext"))
             .as("cond")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "statement")
             .order().by(Dom.Syn.E.ORD)
             .inV().out(Dom.SYN)
             .<Vertex>union(
                 __.<Vertex>limit(1)
                    .addE(Dom.SEM).from("cond")
                    .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                    .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH)
                    .sideEffect(GremlinUtils.setEdgeOrd()).inV(),
                __.<Vertex>skip(1)
                    .addE(Dom.SEM).from("cond")
                    .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                    .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH)
                    .sideEffect(GremlinUtils.setEdgeOrd()).inV())
             .iterate();
        }

        // Sends a 'last' edge from each 'block statement' node to its last nested node.
        // This will be either a block, or a conditional.
        private static void findLastStatements(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM)
             .has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL).inV()
             .repeat(__.out(Dom.SYN))
             .emit(__.has(Dom.Syn.V.CLASS, "BlockStatementContext"))
             .as("block")
             .local(
                __.outE(Dom.SEM)
                // .has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST)
                  .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.STATEMENT),
                      __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST))
             .order().by(Dom.Sem.ORD, Order.desc)
             .limit(1)
             .inV()
             .addE(Dom.SEM).from("block"))
             .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
             .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.LAST)
             .sideEffect(GremlinUtils.setEdgeOrd())
             .iterate();
        }

        // For each block that nests other blocks:
        // Finds all those blocks of a control definition 
        // that can be the last block of that control.
        // Note that there can be multiple potential last blocks because of 
        // conditionals.
        // This is a transitive closure of 'body', 'trueBranch', 'falseBranch',
        // and those 'last' edges that point to the nested block
        // note: 'last' denotes the last position, so last can point to statements as well, but return only points to last blocks. (this way return is always a continuation, and can be used in control flow analysis.) 
        // IMPROVEMENT: not counting conditionals, this is now polynomial time but it could be linearized if higher nodes reused the return statements of their last-nodes.
        private static void findReturnStatements(GraphTraversalSource g) {

                g.E().hasLabel(Dom.SEM)
                 .or(__.has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP)
                       .has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL),
                     __.has(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                      .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.BODY),
                          __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH),
                          __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH),
                          __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST)))
                .inV().as("controlRoot") 

                .repeat(__.outE(Dom.SEM)
                          .or(__.has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP)
                                .has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL),
                              __.has(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                                .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.BODY),
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH),
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH),
                                    __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.LAST).inV().inE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST)))
                        .inV())
                .until(__.outE(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                        .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.BODY),
                            __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.TRUE_BRANCH),
                            __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.FALSE_BRANCH),
                            __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.LAST)
                              .inV().inE(Dom.SEM).has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NEST))
                        .count().is(0))
                .addE(Dom.SEM).from("controlRoot")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.CONTROL)
                .property(Dom.Sem.ROLE, Dom.Sem.Role.Control.RETURN)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();

        }
    }

    private static class Instantiation {

        private static void analyse(GraphTraversalSource g) {

            findInstantiation(g);
            findTypeRefName(g);
            findName(g);
            findArguments(g);
            findInvokedControls(g);
        }

        private static void findInstantiation(GraphTraversalSource g) {
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "InstantiationContext")
            .addE(Dom.SEM).from(g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, 0))
            .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).property(Dom.Sem.ROLE, Dom.Sem.Role.Top.INSTANTIATION)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();
        }

        private static void findTypeRefName(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.INSTANTIATION).inV()
             .as("insta")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeRef").inV()
             .repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .addE(Dom.SEM).from("insta")
             .sideEffect(GremlinUtils.setEdgeOrd())
             .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.INSTANTIATION)
             .property(Dom.Sem.ROLE, Dom.Sem.Role.Instantiation.TYPE)
             .iterate();
        }


        private static void findName(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.INSTANTIATION).inV()
             .as("insta")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
             .repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .addE(Dom.SEM).from("insta")
             .sideEffect(GremlinUtils.setEdgeOrd())
             .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.INSTANTIATION)
             .property(Dom.Sem.ROLE, Dom.Sem.Role.Instantiation.NAME)
             .iterate();
        }

        private static void findArguments(GraphTraversalSource g) {
            g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.INSTANTIATION).inV()
                .as("insta")
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "ArgumentContext"))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                .addE(Dom.SEM).from("insta")
                .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.INSTANTIATION).property(Dom.Sem.ROLE, Dom.Sem.Role.Instantiation.ARGUMENT)
                .sideEffect(GremlinUtils.setEdgeOrd())
                .iterate();
        }

        private static void findInvokedControls(GraphTraversalSource g) {
            List<Map<String, Vertex>> invoked = 
                g.E().hasLabel(Dom.SEM).has(Dom.Sem.DOMAIN, Dom.Sem.Domain.INSTANTIATION).has(Dom.Sem.ROLE, Dom.Sem.Role.Instantiation.ARGUMENT).inV()
                .as("arg")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .as("name")
                .<Vertex>select("arg", "name")
                .toList();

            for (Map<String,Vertex> m : invoked) {
                g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.NODE_ID, 0).outE(Dom.SEM)
                 .or(__.has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.PARSER),
                     __.has(Dom.Sem.DOMAIN, Dom.Sem.Domain.TOP).has(Dom.Sem.ROLE, Dom.Sem.Role.Top.CONTROL))
                 .inV()
                 .filter(__.outE(Dom.SEM)
                           .or(__.has(Dom.Sem.ROLE, Dom.Sem.Role.Parser.NAME),
                              __.has(Dom.Sem.ROLE, Dom.Sem.Role.Control.NAME))
                           .inV().has(Dom.Syn.V.VALUE, P.eq(m.get("name").value(Dom.Syn.V.VALUE))))
                 .addE(Dom.SEM).from(__.V(m.get("arg")))
                 .property(Dom.Sem.DOMAIN, Dom.Sem.Domain.INSTANTIATION).property(Dom.Sem.ROLE, Dom.Sem.Role.Instantiation.INVOKES)
                 .sideEffect(GremlinUtils.setEdgeOrd())
                 .iterate();
            }
        }
    }

    public static class Symbol {
        public static void analyse(GraphTraversalSource g){
            resolveNames(g);
            resolveTypeRefs(g);
            localScope(g);
            parameterScope(g);
            fieldAndMethodScope(g);
        }

        public static void resolveNames(GraphTraversalSource g){
            g.V().hasLabel(Dom.SYN)
            .or(__.has(Dom.Syn.V.CLASS, "HeaderTypeDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "ExternDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "StructTypeDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "StructFieldContext"),
                __.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "TableDeclarationContext"),
                __.has(Dom.Syn.V.CLASS, "FunctionPrototypeContext"),
                __.has(Dom.Syn.V.CLASS, "ParameterContext"))
            .as("root")
            .outE(Dom.SYN)
            .or(__.has(Dom.Syn.E.RULE, "name"),
                __.has(Dom.Syn.E.RULE, "nonTypeName"))
            .inV()
            .repeat(__.out())
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .addE(Dom.SYMBOL).from("root")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();

        }

        // TODO typeRefs can be prefixed
        public static void resolveTypeRefs(GraphTraversalSource g){
            g.E().hasLabel(Dom.SYN).has(Dom.Syn.E.RULE, "typeRef").as("e")
            .outV().as("typedExpr")
            .select("e")
            .inV().outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeName").inV() 

            .repeat(__.out())
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))

            .addE(Dom.SYMBOL).from("typedExpr")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
            .sideEffect(GremlinUtils.setEdgeOrd())
            .iterate();

            g.E().hasLabel(Dom.SYMBOL)
             .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
             .as("typeNameNode")
             .values("value").as("typeName")

             .V().hasLabel(Dom.SYN)
             .or(__.has(Dom.Syn.V.CLASS, "HeaderTypeDeclarationContext"),
                 __.has(Dom.Syn.V.CLASS, "ExternDeclarationContext"),
                 __.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
                 __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"),
                 __.has(Dom.Syn.V.CLASS, "StructTypeDeclarationContext")) 

             .filter(__.outE(Dom.SYMBOL).has(Dom.Sem.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                       .inV().values("value").where(P.eq("typeName")))
             .addE(Dom.SYMBOL).from("typeNameNode")
             .property(Dom.Symbol.ROLE, Dom.Symbol.Role.REFERS_TO)
             .sideEffect(GremlinUtils.setEdgeOrd())
             .iterate();
        }

        // subproblems: 
        // - prefixed names
        // - local scopes
        // - indirect references (this requires dataflow)

        // TODO prefixed names can introduce bugs
        // - e.g. local declaration of 'x' will scope struct fields names 'x' (in case they are used)
        // - not sure, but probably prefixed names can be omitted altogether 
        public static void localScope(GraphTraversalSource g){
            // inside a block, all statements to the right of the declaration are in the scope (until the end of the block)

            // select variable or constant declarations and their names
            g.V().hasLabel(Dom.SYN)
             .or(__.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
                 __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"))
             .as("decl")
             .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
             .values("value")
             .as("declaredName")

            // select matching terminals inside the block after the declaration
            // NOTE: the syntax tree contains the statements list reversed (rightmost in code is topmost in tree)
            // - go up until the list-node of the declaration (to omit it for collection)
             .<Vertex>select("decl")
             .repeat(__.in(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "StatOrDeclListContext"))

             // - keep going up and collect the list-nodes
             .repeat(__.in(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "BlockStatementContext"))
             .emit(__.has(Dom.Syn.V.CLASS, "StatOrDeclListContext"))
             .outE().has(Dom.Syn.E.RULE, "statementOrDeclaration").inV()

             // - collect matching terminals under each list-node subtree
             .repeat(__.out(Dom.SYN))
             .emit(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                     .values("value")
                     .where(P.eq("declaredName")))
             .dedup()

             .addE(Dom.SYMBOL).from("decl")
             .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
             .sideEffect(GremlinUtils.setEdgeOrd())
             .iterate();
        }

        // TODO is there variable covering? (e.g. action parameters cover control parameters?)
        // - if yes, start adding edges from the bottom, and don't add new edges to those who already have one
        public static void parameterScope(GraphTraversalSource g){
            g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ParameterContext")
             .as("decl")
             .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
             .values("value")
             .as("declaredName")
             .<Vertex>select("decl")

             .repeat(__.in(Dom.SYN))
             .until(__.or(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"),
                          __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                          __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext")))

             .outE(Dom.SYN)
             .or(__.has(Dom.Syn.E.RULE, "parserLocalElements"),
                 __.has(Dom.Syn.E.RULE, "parserStates"),
                 __.has(Dom.Syn.E.RULE, "controlLocalDeclarations"),
                 __.has(Dom.Syn.E.RULE, "controlBody"),
                 __.has(Dom.Syn.E.RULE, "blockStatement")) // action
             .inV()
             .repeat(__.out(Dom.SYN))
             .emit(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                     .values("value")
                     .where(P.eq("declaredName")))
             .dedup()

             .addE(Dom.SYMBOL).from("decl")
             .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
             .sideEffect(GremlinUtils.setEdgeOrd())
             .iterate();
        }

        // TODO there are probably field and method refs other than lvalue as well. (e.g. in expressions)
        // TODO mark_to_drop is an extern function prototype without an enclosing extern. set its scope too!
        @SuppressWarnings("unchecked")
        public static void fieldAndMethodScope(GraphTraversalSource g){
        // NOTE: possible gremlin bug: this was originally one query, but for some reason a select kept losing a variable

            List<Map<String, Object>> lvArities =
                g.V().hasLabel(Dom.SYN)
                    // select top-most lvalue elements (i.e. those whose lvalue parent has no lvalue parent)
                    .has(Dom.Syn.V.CLASS, "LvalueContext")
                    .filter(__.inE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").outV()
                            .inE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue")
                            .count().is(0))
                    .as("lv")

                    // in case this is a method call, find out the arity (otherwise this will return 0)
                    .map(__.inE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").outV()
                            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").inV()
                            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonEmptyArgList").inV())
                            .emit()
                            .count())
                    .map(t -> (Long) t.get())
                    .as("arity")
                    .select("lv", "arity")
                    .toList();

            // process the lvalue chains
            for(Map<String, Object> lvArity : lvArities){
                Vertex lv = (Vertex) lvArity.get("lv");
                Long arity = (Long) lvArity.get("arity");

                // collect each element in the chain. the chain must be reversed, but fold() cannot be used, because it forgets the arity variable, so as a workaround we go to the bottom and go to the up while collecting the elements
                g.V(lv)

                .emit()
                .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").inV())
                .fold().map(t -> { List<Vertex> vs = t.get(); Collections.reverse(vs); return vs;}).unfold()

                .<Vertex>coalesce(

                    // for the first element: find out which declaration scopes the name, and find its type. store it. (we don't add new edges here, since the scope is already set for this element)
                    __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "prefixedNonTypeName").inV()
                    .repeat(__.out(Dom.SYN))
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                    .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, "scopes").outV()
                    .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, "hasType").inV()
                    .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, "refersTo").inV()
                    .aggregate("currentType"),

                    // for every element other than the first:
                    __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()

                        // find the name in use
                        .repeat(__.out(Dom.SYN))
                        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                        .as("useNode")
                        .values("value")
                        .as("useName")

                        // load the current context (struct, extern)
                        .flatMap(__.cap("currentType").<Vertex>unfold()) // unfold loses the name, but flatmap prevents it

                        // find the field and method declaration that declares the name (and has the right arity). add the use into the scope of the declaration.
                        .repeat(__.out(Dom.SYN))
                        .until(
                            __.or(__.has(Dom.Syn.V.CLASS, "StructFieldContext"),
                                    __.has(Dom.Syn.V.CLASS, "FunctionPrototypeContext"))
                                .as("declaration")
                                // match name
                                .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
                                .values("value")
                                .where(P.eq("useName"))

                                // match arity
                                .select("declaration")
                                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "parameterList").inV()
                                .map(__.repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonEmptyParameterList").inV())
                                    .emit()
                                    .count())
                                .is(P.eq(arity))
                                )
                        .sideEffect(
                            __.addE(Dom.SYMBOL).to("useNode")
                                .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                                .sideEffect(GremlinUtils.setEdgeOrd()))

                        // in case the type of the declaration was found before, make the type declaration the current context.
                        .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                        .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.REFERS_TO).inV()
                        .sideEffect(t -> { ((BulkSet<Vertex>) t.sideEffects("currentType")).clear(); } )
                        .aggregate("currentType"))
                .iterate();
            }
        }
    }

 // not sure if useful
//    public static class Structure {
//        public static void analyse(GraphTraversalSource g){
//
//
//        }
//        public static void controlTables(GraphTraversalSource g){
//            g.V().hasLabel(Dom.SYN)
//             .has(Dom.Syn.V.CLASS, "ControlDeclarationContext").as("ctl")
//             .repeat(__.outE(Dom.SYN)
//                       .has(Dom.Syn.E.RULE, "controlLocalDeclarations").inV())
//             .emit(__.has(Dom.Syn.V.CLASS,"TableDeclarationContext"))
//             .addE(Dom.STRUCT).from("ctl")
//             .property(Dom.Struct.ROLE, Dom.Struct.Role.TABLE)
//             .sideEffect(GremlinUtils.setEdgeOrd())
//             .iterate();
//        }
//        public static void controlActions(GraphTraversalSource g){
//            g.V().hasLabel(Dom.SYN)
//             .has(Dom.Syn.V.CLASS, "ControlDeclarationContext").as("ctl")
//             .repeat(__.outE(Dom.SYN)
//                       .has(Dom.Syn.E.RULE, "controlLocalDeclarations").inV())
//             .emit(__.has(Dom.Syn.V.CLASS,"ActionDeclarationContext"))
//             .addE(Dom.STRUCT).from("ctl")
//             .property(Dom.Struct.ROLE, Dom.Struct.Role.ACTION)
//             .sideEffect(GremlinUtils.setEdgeOrd())
//             .iterate();
//        }
//    }

}