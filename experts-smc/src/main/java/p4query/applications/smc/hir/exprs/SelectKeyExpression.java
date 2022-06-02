/**
 * Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
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
 *
 * Author: Dániel Lukács, 2022
 */
package p4query.applications.smc.hir.exprs;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Goto;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Top;
import p4query.applications.smc.lir.typing.Int;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.applications.smc.lir.typing.UnresolvedVertexLabel;
import p4query.ontology.Dom;

public class SelectKeyExpression implements Expression {

//    private CustomStorageReference boolStore; // not needed, because memcmp return value will be on the stack
    private Vertex src;
    private Declaration parent;
    private List<Pattern> patterns;
    private List<IRType> headTypes;

    private static class Pattern {
        Vertex vertex;
        // TODO make this an enum
        String type;
        Expression expr;

        Pattern(Vertex vertex, String type, Expression expr) {
            this.vertex = vertex;
            this.type = type;
            this.expr = expr;
        }
    }

    public SelectKeyExpression(CompilerState state, Vertex selectCase, IRType typeHint) throws UnableToParseException {
        GraphTraversalSource g = state.getG();

        if (!g.V(selectCase).outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression").hasNext()){
            throw new UnableToParseException(SelectKeyExpression.class, selectCase);
        }

        this.src = selectCase;
        this.parent = state.getParentDecl();
        this.headTypes = findHeadTypes(state, selectCase, typeHint, g);

        List<Vertex> patternPars;

        // try whether this is a select with a single pattern
        patternPars = 
                g.V(selectCase).outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression").inV()
                 .outE(Dom.SYN).has(Dom.Syn.E.RULE, "simpleKeysetExpression").inV()
                 .toList();

        // it was not a select with a single pattern. handle it as a mult-patterned select
        if(patternPars.isEmpty()){
            patternPars = 
                g.V(selectCase).outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression").inV()
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "tupleKeysetExpression").inV()
                    .emit()
                    .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "simpleExpressionList").inV())
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "simpleKeysetExpression").inV()
                    .toList();
        }

//        try {} catch(NoSuchElementException e){
//            throw new IllegalArgumentException(
//                String.format("Only select cases with simple keys are handled currently. Maybe this has a keylist? (not implemented) Vertex: %s", g.V(selectCase).elementMap().next()));
//        }

        this.patterns = new LinkedList<>();

        int i = 0;
        for (Vertex patternPar : patternPars) {
            IRType headType = headTypes.get(i);
            this.patterns.add(handlePatternPar(state, selectCase, headType, g, patternPar));
            i += 1;
        }
    }

    private List<IRType> findHeadTypes(CompilerState state, Vertex selectCase, IRType typeHint, GraphTraversalSource g){
        List<Vertex> headExprs = 
          g.V(selectCase)
            .repeat(__.in(Dom.SYN))
            .until(__.has(Dom.Syn.V.CLASS, "SelectExpressionContext"))
            .identity()
            .repeat(__.outE(Dom.SYN).has(Dom.Syn.E.RULE, "expressionList").inV())
            .emit()
            .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
            .toList();
        Collections.reverse(headExprs);
        List<IRType> headTypes = headExprs.stream().map(v -> Expression.Factory.create(state, v, typeHint).getTypeHint()).collect(Collectors.toList());
        return headTypes;
    }

    private Pattern handlePatternPar(CompilerState state, Vertex selectCase, IRType typeHint, GraphTraversalSource g,
            Vertex patternPar) {
        Map<String, Object> patternM = 
            g.V(patternPar).outE(Dom.SYN)
            .or(__.has(Dom.Syn.E.RULE, "expression"),
                __.has(Dom.Syn.E.RULE, "DEFAULT"),
                __.has(Dom.Syn.E.RULE, "DONTCARE"))
            .project("pattern", "type")
            .by(__.inV())
            .by(__.values(Dom.Syn.E.RULE))
            .next();

        String patternType = (String) patternM.get("type");
        Vertex patternVert = (Vertex) patternM.get("pattern");

        if(patternType.equals("DEFAULT") || patternType.equals("DONTCARE")){
            return new Pattern(patternVert, patternType, null);

        } else if(patternType.equals("expression")){

            // NOTE this will be a storage reference or a literal. the necessary work is done by those classes.
            return 
                new Pattern(patternVert, 
                            patternType, 
                            Expression.Factory.create(state, patternVert, typeHint));

   //            IRType type = typeFactory.create("BOOLEAN", 1, this);
   //            String localName = procDef.addTemporary(type);
   //            CustomStorageReference result = new CustomStorageReference("SelectKeyExpression", localName, type, this);
   //            this.boolStore = result;

        } else {
            throw new IllegalStateException(
                String.format("Select case has an unknown kind of key. Vertex: %s", g.V(selectCase).elementMap().next()));
        }
    }


    @Override
    public String toString() {
        return "SelectKeyExpression[ p4syntax=" + toP4Syntax() + "]";
    }

    @Override
    public String toP4Syntax() {
        StringBuilder sb = new StringBuilder();
        sb.append("HEAD ~= ");
        String delim = "";
        for (Pattern pattern : patterns) {
            sb.append(delim); 
            delim = ", ";
            if(pattern.type.equals("expression")){
                sb.append(pattern.expr.toP4Syntax()); 
            } else {
                sb.append(pattern.type); 
            }
        }
        return sb.toString();
    }

    @Override
    public StorageReference getStorageReference() {
        throw new IllegalStateException("This is a SelectKeyExpression, yet someone called getStorageReference()");

//        if(patternType.equals("expression"))
//            return patternExpr.getStorageReference();
//            return boolStore;
//        else
//            throw new IllegalStateException("Pattern type is " + patternType + ", yet someone called getStorageReference()");
    }

    // TODO this probably has bugs, i have to check what 'src' is in the instruction 'new IfEq(new UnresolvedVertexLabel(src, "jump to next case if not " + this.toP4Syntax(), parent));'
    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
        // a case key consists of multiple patterns. go through each. if you find a non-matching pattern, the case is non-matching, go to next case. if all pattern is matching, the case is matching, go to right hand side. 

        List<StackInstruction> match = Arrays.asList(
            new Comment("match"), 
            new Pop() // pop original head address
        );

        LinkedList<StackInstruction> insts = new LinkedList<>();

        insts.add(new Comment(this.toP4Syntax()));
        int i = 0;
        for (Pattern pattern : patterns) {

            if(pattern.type.equals("expression")){

                // list of head expression addresses are already on the stack.
                // src of memcmp: address of i-th head expression
                insts.add(new Top());
                insts.add(new Const(new Int(i, "index of head expression")));
                insts.add(new Add());
                insts.add(new GetField());

                // dst of memcmp
                insts.addAll(pattern.expr.compileToLIR(local, global));  // TODO this generates decimal, it should be binary
    //            insts.addAll(this.boolStore.compileToLIR(local, global));

                // size of memcmp
                insts.add(new Const(new Size(pattern.expr.getTypeHint().getSize(), pattern.expr.toP4Syntax())));
                insts.add(
                    new Invoke(
                        new UnresolvedNameLabel("stdlib","memcmp",""), 
                        new Size(3, "left, right, length")));
                insts.add(new Comment("return value is used"));

                // 

                // jump if memcmp returns 0. keep going otherwise
                insts.add(new IfEq(new UnresolvedVertexLabel(src, "jump to next case if not " + this.toP4Syntax(), parent)));

            } else if (pattern.type.equals("DONTCARE")) {
                // nothing to check, skip
            } else if (pattern.type.equals("DEFAULT")){
                // nothing to check, skip
            } else {
                throw new IllegalStateException("Pattern type is " + pattern.type + ", only 'expression', 'DONTCARE' and 'DEFAULT'  are accepted");
            }

            i += 1;
        }

        // pop original head address 
        insts.add(new Goto(new UnresolvedInstructionLabel(match.get(0), "match")));
        //   insts.addAll(onNoMatch);
        insts.addAll(match);

        return insts;
    }

    @Override
    public IRType getTypeHint() {

        throw new IllegalStateException("This is a SelectKeyExpression, yet someone called getStorageReference()");
        // if(patternType.equals("expression"))
        //     return boolStore.getSizeHint();
        // else
        //     throw new IllegalStateException("Pattern type is " + patternType + ", yet someone called getSizeHint()");
    }

}
