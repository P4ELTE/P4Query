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
package p4query.experts.symboltable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.function.Lambda;
import org.codejargon.feather.Provides;

import p4query.ontology.Dom;
import p4query.ontology.Status;
import p4query.ontology.analyses.AbstractSyntaxTree;
import p4query.ontology.analyses.SymbolTable;
import p4query.ontology.analyses.SyntaxTree;


// TODO this module should be rewritten, espec. w.r.t. the "fix" prefixed methods 
public class SymbolTableImpl 
{

    // NOTE syntax maybe too permissive with expressions: (~ (13 >> true) . etherType) is a syntactically valid expression, even though '~', '>>', and '.' are reserved tokens. for this reason I decided to handle case-by-case

    @Provides
    @Singleton
    @SymbolTable
    public Status analyse(GraphTraversalSource g, @SyntaxTree Status s, @AbstractSyntaxTree Status a){
        System.out.println(SymbolTable.class.getSimpleName() +" started.");

        resolveNames(g);
        resolveTypeRefs(g);
        parserStateScopes(g);
        localScope(g);
        parameterScope(g);
        fieldAndMethodScope(g);
        actionRefs(g);
        tableApps(g);
        packageInstantiations(g);
        controlAndParserInstantiations(g);

        fixBaseTypeType(g);
        fixTypedefs(g);
        fixEnums(g);
        fixMissingScopes(g);
        fixGlobalConstantScopes(g);

        System.out.println(SymbolTable.class.getSimpleName() +" complete.");
        return new Status();
    }


    public static void resolveNames(GraphTraversalSource g) {
        g.V().hasLabel(Dom.SYN)
        .or(__.has(Dom.Syn.V.CLASS, "HeaderTypeDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "ExternDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "StructTypeDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "StructFieldContext"),
            __.has(Dom.Syn.V.CLASS, "VariableDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "TableDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "FunctionPrototypeContext"),
            __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "TableDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "ParserStateContext"),
            __.has(Dom.Syn.V.CLASS, "PackageTypeDeclarationContext"),
            __.has(Dom.Syn.V.CLASS, "ParameterContext"))
        .as("root")
        .optional(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "parserTypeDeclaration").inV())
        .optional(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlTypeDeclaration").inV())
        .outE(Dom.SYN)
        .or(__.has(Dom.Syn.E.RULE, "name"),
            __.has(Dom.Syn.E.RULE, "nonTypeName"))
        .inV()
        .repeat(__.out())
        .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
        .addE(Dom.SYMBOL).from("root")
        .property(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
        
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
            .addE(Dom.SYMBOL).to("typeNameNode")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
            
            .iterate();
    }

    @SuppressWarnings("unchecked")
    private static void parserStateScopes(GraphTraversalSource g) {
        g.V().hasLabel(Dom.SYN)
            // find all names that refer to parser states
            .has(Dom.Syn.V.CLASS, "StateExpressionContext")
            .coalesce(
                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV(), 
                __.repeat(__.out(Dom.SYN))
                  .until(__.has(Dom.Syn.V.CLASS, "SelectCaseContext")) 
                  .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()) 
            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .as("nameNode")
            .values("value").as("name")

            // find all parsers states in that parser
            .<Vertex>select("nameNode")
            .repeat(__.in(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))

            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "ParserStateContext"))
            .as("decl")

            // select the one that declares that name
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
            .values("value")
            .where(P.eq("name"))
            .addE(Dom.SYMBOL).from("decl").to("nameNode")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
            .iterate();
    }

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
            
            .iterate();
    }


    // TODO is there variable covering? (e.g. action parameters cover control parameters?)
    // - if yes, start adding edges from the bottom, and don't add new edges to those who already have one
    public static void parameterScope(GraphTraversalSource g){
        // find parameters
        g.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ParameterContext")
            .as("decl")
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
            .values("value")
            .as("declaredName")

            .<Vertex>select("decl")

            // go up in the tree to find the procedure that owns the parameter
            .repeat(__.in(Dom.SYN))
            .until(__.or(__.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"),
                        __.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                        __.has(Dom.Syn.V.CLASS, "ActionDeclarationContext")))

            // go down into the procedure body (or bodies, in case of parsers)
            .outE(Dom.SYN)
            .or(__.has(Dom.Syn.E.RULE, "parserLocalElements"),
                __.has(Dom.Syn.E.RULE, "parserStates"),
                __.has(Dom.Syn.E.RULE, "controlLocalDeclarations"),
                __.has(Dom.Syn.E.RULE, "controlBody"),
                __.has(Dom.Syn.E.RULE, "blockStatement")) // action
            .inV()

            // find all terminals that refer to the name declared by the parameter
            .repeat(__.out(Dom.SYN))
            .emit(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                    .values("value")
                    .where(P.eq("declaredName")))
            .dedup()

            .addE(Dom.SYMBOL).from("decl")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
            
            .iterate();
    }

    // TODO eliminate Lambda.function
    // NOTE this handles both lvalues and expressions that refer to struct fields and extern methods
    // NOTE does not handle expressions referring to parsers, actions, controls 
    @SuppressWarnings("unchecked")
    public static void fieldAndMethodScope(GraphTraversalSource g){
    // NOTE: possible gremlin bug: this was originally one query, but for some reason a select kept losing a variable

        List<Map<String, Object>> lvArities =
            g.V().hasLabel(Dom.SYN)
                // select top-most lvalue elements (i.e. those whose lvalue parent has no lvalue parent)
                .or(__.has(Dom.Syn.V.CLASS, "LvalueContext"),
                    __.has(Dom.Syn.V.CLASS, "ExpressionContext").outE(Dom.SYN).has(Dom.Syn.E.RULE, "DOT"),
                    __.has(Dom.Syn.V.CLASS, "ExpressionContext").outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList"))
                .filter(__.or(__.inE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue").outV()
                                .inE(Dom.SYN).has(Dom.Syn.E.RULE, "lvalue"),
                                __.inE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").outV()
                                .inE(Dom.SYN).has(Dom.Syn.E.RULE, "expression")
                                )
                            .count().is(0))
                .as("lv")

                // in case this is a method call, find out the arity (otherwise this will return 0)
                .map(__.inE(Dom.SYN)
                        .or(__.has(Dom.Syn.E.RULE, "lvalue"), 
                            __.has(Dom.Syn.E.RULE, "expression")).outV()
                        .outE(Dom.SYN).has(Dom.Syn.E.RULE, "argumentList").inV()
                        .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "nonEmptyArgList").inV())
                        .emit()
                        .count())
//                    .map(t -> (Long) t.get())
                .as("arity")
                .select("lv", "arity")
                .toList();

        // process the lvalue chains
        for(Map<String, Object> lvArity : lvArities){
            Vertex lv = (Vertex) lvArity.get("lv");
            Long arity = (Long) lvArity.get("arity");

            // collect each element in the chain. reverse the chain. 
            g.V(lv)

            .emit()
            .repeat(__.outE(Dom.SYN)
                        .or(__.has(Dom.Syn.E.RULE, "lvalue"),
                            __.has(Dom.Syn.E.RULE, "expression"))
                        .inV())

//                .fold().map(t -> { List<Vertex> vs = t.get(); Collections.reverse(vs); return vs;}).unfold()
            .fold().map(Lambda.function("{t ->  List<Vertex> vs = t.get() \n Collections.reverse(vs) \n return vs \n }")).unfold()

            // for lvalues: the first (or the only) element is always "prefixedNonTypeName", the rest are "name"
            // for expressions, the first is 'nonTypeName'
            .outE(Dom.SYN).or(__.has(Dom.Syn.E.RULE, "prefixedNonTypeName"), 
                                __.has(Dom.Syn.E.RULE, "nonTypeName"), 
                                __.has(Dom.Syn.E.RULE, "name")).inV()
            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .<Vertex>coalesce(

                // if an element already has a scope, set the current context to the enclosing type of the declaration 
                // - e.g. in "hdr.ipv4.ttl" the hdr can be scoped by paramater, we need its type to resolve which field scopes ipv4
                __.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
                    .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                    .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
                    .aggregate("currentType"),

                // otherwise, resolve the scope (using the current context or the defaults), add an edge, and set the current context to the type of the declaration
                // - e.g. to process "ipv4" in "hdr.ipv4.ttl", we will search for the field with a matching name among the fields of whatever type "hdr" was
                // - e.g. in "mark_to_drop()" there is only one element and it is probably not scoped yet. we have to search for its name among the extern functions.
                __.<Vertex>identity()
                    // store the name in use
                    .as("useNode")

//                        .sideEffect(t -> System.out.println(t.get().value("value").toString() + "/" + arity))
                    .values("value")
                    .as("useName")

                    // load the current context (struct, extern), or search among global extern functions
                    .coalesce(
                        __.flatMap(__.cap("currentType").<Vertex>unfold()), // unfold loses the name, but flatmap prevents it
                        __.V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ExternDeclarationContext")
                            .filter(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "functionPrototype")))

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
//                                .sideEffect(t -> System.out.println("- name match"))

                            // match arity
                            .select("declaration")
                            .coalesce(
                                __.outE(Dom.SYN).has(Dom.Syn.E.RULE, "parameterList").inV()
                                    .map(__.repeat(__.outE(Dom.SYN)
                                            .has(Dom.Syn.E.RULE, "nonEmptyParameterList").inV())
                                            .emit()
                                            .count()),
                                __.constant(0L))
                            .is(P.eq(arity))
//                                .sideEffect(t -> System.out.println("- arity match"))
                            )

                    .sideEffect(
                        __.addE(Dom.SYMBOL).to("useNode")
                            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                            )

                    // in case the type of the declaration was found before, make the type declaration the current context.
                    .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                    .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()

//                        .sideEffect(t -> { ((BulkSet<Vertex>) t.sideEffects("currentType")).clear(); } )
                    .sideEffect(Lambda.consumer("{ t -> t.sideEffects(\"currentType\").clear() }"))

                    .aggregate("currentType"))

            .iterate();
        }
    }

    // TODO is it legal to refer to action in other namespaces?
    public static void actionRefs(GraphTraversalSource g){
        // from all table declarations
        g.V().hasLabel(Dom.SYN)
            .has(Dom.Syn.V.CLASS, "TableDeclarationContext")
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "tablePropertyList").inV())
            .emit()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "tableProperty").inV()

        // select the name of each action action refs
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "actionList").inV())
            .emit()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "actionRef").inV()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()

            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .as("actRef")
            .values("value").as("actRefName")

        // select those action declarations that declare the name of the currently selected action refs
            .V().hasLabel(Dom.SYN).has(Dom.Syn.V.CLASS, "ActionDeclarationContext").as("decl")
            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
            .values("value")
            .where(P.eq("actRefName"))

            .addE(Dom.SYMBOL).from("decl").to("actRef")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
            
            .iterate();
    }

    // TODO can table names be in other namespaces?
    public static void tableApps(GraphTraversalSource g){
        g.V().hasLabel(Dom.SYN)
            // select all table applications
            .has(Dom.Syn.V.CLASS, "DirectApplicationContext")

            // store the node and name of the applied table
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeName").inV()
            .repeat(__.out(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).as("tableRef")
            .values("value").as("tableRefName")

            // go up to the control declaration that contains the application
            .<Vertex>select("tableRef")
            .repeat(__.in(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"))

            // find the table declaration that declares the name of the applied table
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlLocalDeclarations").inV()
            .emit()
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlLocalDeclarations").inV())
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "controlLocalDeclaration").inV()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "tableDeclaration").inV().as("decl")

            .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME).inV()
            .values("value")
            .where(P.eq("tableRefName"))

            // add edge from declaration to the name node 
            .addE(Dom.SYMBOL).from("decl").to("tableRef")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
            
            .iterate();
    }

    public static void packageInstantiations(GraphTraversalSource g){
        g.V().hasLabel(Dom.SYN)
                .has(Dom.Syn.V.CLASS, "InstantiationContext")
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).as("pkgNode")
                .values("value").as("pkgName")

                .sideEffect(
                __.V().hasLabel(Dom.SYN)
                    .has(Dom.Syn.V.CLASS, "PackageTypeDeclarationContext")
                    .filter(__.outE(Dom.SYMBOL)
                            .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                            .inV()
                            .values("value")
                            .where(P.eq("pkgName"))) 
                    .addE(Dom.SYMBOL).to("pkgNode")
                    .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                    )
            .iterate();
    }
    
    public static void controlAndParserInstantiations(GraphTraversalSource g){
        g.V().hasLabel(Dom.SYN)
                .has(Dom.Syn.V.CLASS, "InstantiationContext")
            // find the argument-instantiations of the package instantiation
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "ArgumentContext"))
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
                .repeat(__.out(Dom.SYN))
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")).as("argNode")
                .values("value").as("argName")

            // find controls and parser that declare the same name
                .sideEffect(
                __.V().hasLabel(Dom.SYN)
                    .or(__.has(Dom.Syn.V.CLASS, "ControlDeclarationContext"),
                        __.has(Dom.Syn.V.CLASS, "ParserDeclarationContext"))
                    .filter(__.outE(Dom.SYMBOL)
                            .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                            .inV()
                            .values("value")
                            .where(P.eq("argName"))) 
                    .addE(Dom.SYMBOL).to("argNode")
                    .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                    )
            .iterate();
                    
    }


    private void fixBaseTypeType(GraphTraversalSource g) {
        g.V().or(__.has(Dom.Syn.V.CLASS, "StructFieldContext"),
                 __.has(Dom.Syn.V.CLASS, "ConstantDeclarationContext"))
              .as("field")
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeRef").inV()
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "baseType").inV().as("baseType")
             .addE(Dom.SYMBOL)
             .property(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
             .from("field").to("baseType")
             .iterate();
    }

    // TODO currently only works for basetype typedefs
    private static void fixTypedefs(GraphTraversalSource g) {
        List<Map<String, Object>> decls =
            g.V().has(Dom.Syn.V.CLASS, "TypedefDeclarationContext")
                .project("decl", "type", "name")
                .by(__.identity())
                .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "typeRef").inV()
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "baseType").inV())
                .by(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
                    .repeat(__.outE(Dom.SYN).inV())
                    .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl")))
                .toList();

        List<Map<String, Object>> typeUsages =
            g.E().hasLabel(Dom.SYMBOL)
                 .has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                 .inV()
                 .has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                 .project("vert", "name")
                 .by(__.identity())
                 .by(__.values("value"))
                 .toList();

        // for faster lookup of terminalnodes by name
        Map<String, List<Vertex>> typeUsagesByName = new HashMap<>();
        for (Map<String,Object> map : typeUsages) {
            Vertex vert = (Vertex) map.get("vert");
            String name = (String) map.get("name");
            
            if(typeUsagesByName.containsKey(name)){
                typeUsagesByName.get(name).add(vert);
            } else {
                LinkedList<Vertex> vs = new LinkedList<>();
                vs.add(vert);
                typeUsagesByName.put(name, vs);
            }
        }
             
        for (Map<String,Object> map : decls) {
            Vertex decl = (Vertex) map.get("decl"); 
            Vertex type = (Vertex) map.get("type"); 
            Vertex nameNode = (Vertex) map.get("name"); 
            String name = (String) g.V(nameNode).values("value").next();

            g.addE(Dom.SYMBOL)
                .property(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                .from(decl).to(nameNode)
                .iterate();

            g.addE(Dom.SYMBOL)
                .property(Dom.Symbol.ROLE, Dom.Symbol.Role.ALIASES_TYPE)
                .from(decl).to(type)
                .iterate();

            if(!typeUsagesByName.containsKey(name))
                continue;

            g.V(typeUsagesByName.get(name))
             .addE(Dom.SYMBOL)
             .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
             .from(decl)
             .iterate();

        }
    }

    private static void fixEnums(GraphTraversalSource g){

        g.V().has(Dom.Syn.V.CLASS, "EnumDeclarationContext").as("enum")
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "name")
            .inV()
            .repeat(__.outE(Dom.SYN).inV())
            .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .addE(Dom.SYMBOL).from("enum")
            .property(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
            .iterate();

        List<Map<String, Object>> enumNames = 
            g.V().has(Dom.Syn.V.CLASS, "EnumDeclarationContext").as("enum")
             .outE(Dom.SYMBOL)
             .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
             .inV()
             .values("value").as("name")
             .select("enum", "name")
             .toList();


        for (Map<String,Object> m : enumNames) {
            Vertex enu = (Vertex) m.get("enum");
            String name = (String) m.get("name");

            g.V().has(Dom.Syn.V.VALUE, name)
                 .has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                 .not(__.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME))
                 .addE(Dom.SYMBOL).from(enu)
                 .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                 .iterate();
        }

        List<Map<String, Object>> enumFieldNames = 
            g.V().has(Dom.Syn.V.CLASS, "EnumDeclarationContext").as("enum")
                .outE(Dom.SYN).has(Dom.Syn.E.RULE, "identifierList").inV()
                .repeat(__.outE(Dom.SYN).inV())
                .until(__.has(Dom.Syn.V.CLASS, "NameContext")).as("name")
                .repeat(__.outE(Dom.SYN).inV())
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .values("value").as("val")
                .select("enum", "name", "val")
                .toList();

        for (Map<String,Object> m : enumFieldNames) {
            Vertex enu = (Vertex) m.get("enum");
            Vertex name = (Vertex) m.get("name");
            String value = (String) m.get("val");

            // TODO do an addition filter: only send the edge, if the lhs of the dot expression matches the enum name
            g.V().has(Dom.Syn.V.VALUE, value)
                 .has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                 .not(__.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES))
                 .addE(Dom.SYMBOL).from(name)
                 .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                 .iterate();
        }

    }

    // TODO add this to symbol table (this is the big query, maybe rewrite that to be like this fix)
    // The RHS of 'hdr.ipv4.ttl = hdr.ipv4.ttl - 1;' in basic.p4 was not assigned scope-edges for some reason (bug). 
    // This is just a local fix.
    private static void fixMissingScopes(GraphTraversalSource g) {

        // name terminals with no inbound scope edge
        List<Vertex> noScopes =
            g.V().has(Dom.Syn.V.CLASS, "ExpressionContext")
                .repeat(__.outE(Dom.SYN).inV())
                .until(__.has(Dom.Syn.V.CLASS, "NameContext"))
                .repeat(__.outE(Dom.SYN).inV())
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .not(__.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES))
                .dedup()
                .toList();

        // for each such name terminal, find the dot-expression that contains the terminal
        List<Vertex> topLevDots = 
            g.V(noScopes)
             .repeat(__.inE(Dom.SYN).outV())
             .until(__.and(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "DOT"),
                           __.inE(Dom.SYN).outV().not(__.has(Dom.Syn.E.RULE, "DOT"))))
             .dedup()
             .toList();

        for (Vertex dotExpr : topLevDots) {

            // collect the referenced fields of the dot-expression into a list
            List<Vertex> fields = 
                g.V(dotExpr) 
                .repeat(__.outE(Dom.SYN)
                          .not(__.has(Dom.Syn.E.RULE, "DOT"))
                          .order().by(Dom.Syn.E.ORD, Order.desc) 
                          .inV())
                .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
                .toList();

            Collections.reverse(fields);

            Iterator<Vertex> it = fields.iterator();
            Vertex first = it.next();

            // find the type of the declaration that scopes the first field reference. 
            // note that the first field of dot-expressions should always be a struct or a header 
            Vertex lastStruct = 
                g.V(first)
                .inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV()
                .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE).inV()
                .optional(
                    __.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV())
                .next();

            while(it.hasNext()){

                if(g.V(lastStruct).has(Dom.Syn.V.CLASS, "BaseTypeContext").hasNext()){
                        throw new IllegalStateException("Previous field reference had base-tyoe type, did not expect more fields.");
                }

                // move to the next field reference
                Vertex current = it.next();
                String fieldVal = (String) g.V(current).values("value").next();

                // isValid is not declared anywhere in P4
                if(fieldVal.equals("isValid")) 
                    continue;


// delete this 
//                // if the last field reference declaration has base-type type, add a missing scope edge, and terminate.
//                if(g.V(lastStruct).has(Dom.Syn.V.CLASS, "BaseTypeContext").hasNext()){
//                    g.addE(Dom.SYMBOL).from(lastStruct).to(current)
//                    .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
//                    .iterate();
//
//                    if(it.hasNext())
//                        throw new IllegalStateException("Current field has base-tyoe type, did not expect more fields.");
//                    break;
//                }

                // in the struct that was referred to by the previous field reference, find the first field with the same name as the current field reference.

                Vertex correspStructField  =
                    g.V(lastStruct)
                     .repeat(__.outE(Dom.SYN)
                               .order().by(Dom.Syn.E.ORD, Order.asc)
                               .inV())
                     .until(__.has(Dom.Syn.V.CLASS, "StructFieldContext")
                              .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
                              .inV()
                              .has(Dom.Syn.V.VALUE, fieldVal))
                     .next();

                g.addE(Dom.SYMBOL).from(correspStructField).to(current)
                 .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                 .iterate();

            // find the type of the field declaration that scopes the current field reference. 
                lastStruct = 
                    g.V(correspStructField)
                    .outE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.HAS_TYPE)
                    .inV()
                    .optional(
                        __.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES).outV())
                    .next();
            }

            

        }

    }

    private static void fixGlobalConstantScopes(GraphTraversalSource g) {
        // note: this is copy-paste from fixEnum()

        List<Map<String, Object>> names = 
            g.V().has(Dom.Syn.V.CLASS, "ConstantDeclarationContext").as("const")
             .outE(Dom.SYMBOL)
             .has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME)
             .inV()
             .values("value").as("name")
             .select("const", "name")
             .toList();

        for (Map<String,Object> m : names) {
            Vertex enu = (Vertex) m.get("const");
            String name = (String) m.get("name");

            g.V().has(Dom.Syn.V.VALUE, name)
                 .has(Dom.Syn.V.CLASS, "TerminalNodeImpl")
                 .not(__.inE(Dom.SYMBOL).has(Dom.Symbol.ROLE, Dom.Symbol.Role.DECLARES_NAME))
                 .addE(Dom.SYMBOL).from(enu)
                 .property(Dom.Symbol.ROLE, Dom.Symbol.Role.SCOPES)
                 .toList();
        }
    }

}
