package p4analyser.applications;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.codejargon.feather.Provides;

import java.io.File;
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

  // User interface

  private final Task4AppUI ui = new Task4AppUI();

  @Override
  public AppUI getUI(){
      return ui;
  }

  // Business logic

  @Inject
  private GraphTraversalSource g; 

  @Inject
  @InputP4File 
  private File file;

  @Inject
  @SyntaxTree 
  private Provider<Status> ensureSt;

  @Override
  public Status run(){
      if(ui.synTree)
        ensureSt.get();
      
      System.out.println(g.V().count().next());

      Map<Object, Object> result = analyse(g, "ethernet", "etherType");

	  System.out.println("Task4 done. Result: " + result);

      return new Status();
  }

	public static Map<Object, Object> analyse(GraphTraversalSource g, Object header, Object field) {

		return (
			g.V().has(Dom.Syn.V.CLASS,"SelectExpressionContext")
			.and(
				__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV()
				.and(
					__.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, "hdr"),
					__.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, header),
					__.repeat(__.out(Dom.SYN)).until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).has(Dom.Syn.V.VALUE, field)
				)
			)
			.group()
			.by(
				__.inE(Dom.SYN)
				.has(Dom.Syn.E.RULE, "selectExpression").outV().repeat(__.in(Dom.SYN))
				.until(__.has(Dom.Syn.V.CLASS, "ParserStateContext"))
				.group()
				.by(
					__.inE(Dom.SYN)
					.has(Dom.Syn.E.RULE, "parserState").outV().repeat(__.in(Dom.SYN))
					.until(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))
					.outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserTypeDeclaration").inV()
					.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
					.repeat(__.out(Dom.SYN))
					.until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
					.values(Dom.Syn.V.VALUE)
				)
				.by(
					__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
					.repeat(__.out(Dom.SYN))
					.until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
					.values(Dom.Syn.V.VALUE)
				)
			)
			.by(
				__.outE(Dom.SYN)
				.has(Dom.Syn.E.RULE, "selectCaseList").inV().repeat(__.out(Dom.SYN))
				.until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext"))
				.outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression")
				.inV().repeat(__.out(Dom.SYN))
				.until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
				.values(Dom.Syn.V.VALUE).fold()
			)
		).next();

	}

}
