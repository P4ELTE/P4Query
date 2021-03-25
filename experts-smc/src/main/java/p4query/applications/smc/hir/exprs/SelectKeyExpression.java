package p4query.applications.smc.hir.exprs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.iset.SelectHead;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.IRType.SingletonFactory;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.IfEq;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.Add;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.iset.Store;
import p4query.applications.smc.lir.typing.LocalAddress;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.applications.smc.lir.typing.UnresolvedVertexLabel;
import p4query.ontology.Dom;

public class SelectKeyExpression implements Expression {

    private Vertex patternVert;
    // TODO make this an enum
    private String patternType;
    private Definition procDef;
    private SingletonFactory typeFactory;
    private GraphTraversalSource g;
    private Expression patternExpr;
    private Expression headExpr;
//    private CustomStorageReference boolStore; // not needed, because memcmp return value will be on the stack
    private Vertex src;

    public SelectKeyExpression(GraphTraversalSource g, Vertex selectCase, IRType.SingletonFactory typeFactory, Definition procDef) {
        this.g = g;
        this.typeFactory = typeFactory;
        this.procDef = procDef;
        this.src = selectCase;

        Vertex patternPar;
        try {
            patternPar = 
                g.V(selectCase).outE(Dom.SYN).has(Dom.Syn.E.RULE, "keysetExpression").inV()
                    .outE(Dom.SYN).has(Dom.Syn.E.RULE, "simpleKeysetExpression").inV()
                    .next();
        } catch(NoSuchElementException e){
            throw new IllegalArgumentException(
                String.format("Only select cases with simple keys are handled currently. Maybe this has a keylist? (not implemented) Vertex: %s", g.V(selectCase).elementMap().next()));
        }

        Map<String, Object> patternM = 
            g.V(patternPar).outE(Dom.SYN)
            .or(__.has(Dom.Syn.E.RULE, "expression"),
                __.has(Dom.Syn.E.RULE, "DEFAULT"),
                __.has(Dom.Syn.E.RULE, "DONTCARE"))
            .project("pattern", "type")
            .by(__.inV())
            .by(__.values(Dom.Syn.E.RULE))
            .next();

        this.patternType = (String) patternM.get("type");
        this.patternVert = (Vertex) patternM.get("pattern");

        Vertex selectExpr = 
            g.V(selectCase).repeat(__.inE(Dom.SYN).outV()) 
                .until(__.has(Dom.Syn.V.CLASS, "SelectExpressionContext"))
                .next();
        this.headExpr = SelectHead.findSelectHeadExpression(g, selectExpr, typeFactory, procDef);

        if(patternType.equals("DEFAULT") || patternType.equals("DONTCARE")){
            this.patternExpr = null;

        } else if(patternType.equals("expression")){

            // NOTE this will be a storage reference or a literal. the necessary work is done by those classes.
            this.patternExpr = Expression.Factory.create(g, patternVert, typeFactory, procDef, headExpr.getSizeHint());

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
        return "SelectKeyExpression [patternType=" + patternType + ", " + patternExpr.toP4Syntax() + "]";
    }

    @Override
    public String toP4Syntax() {
        if(patternType.equals("expression"))
            return headExpr.toP4Syntax() + " ~= " + patternExpr.toP4Syntax();
        else 
            return headExpr.toP4Syntax() + " ~= " + patternType;
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

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();

        insts.add(new Comment(this.toP4Syntax()));
        if(patternType.equals("expression")){
            // head address is already on the stack
            insts.addAll(patternExpr.compileToLIR(local, global));
//            insts.addAll(this.boolStore.compileToLIR(local, global));
            insts.add(new Const(new Size(patternExpr.getSizeHint(), patternExpr.toP4Syntax())));
            insts.add(new Invoke(new UnresolvedNameLabel("stdlib","memcmp",""), new Size(3, "left, right, length")));
            insts.add(new Comment("return value is used"));
            insts.add(new IfEq(new UnresolvedVertexLabel(src, "jump if not " + this.toP4Syntax())));
        } else {
            // pop head address
            insts.add(new Pop());
        }

        return insts;
    }

    @Override
    public int getSizeHint() {

        throw new IllegalStateException("This is a SelectKeyExpression, yet someone called getStorageReference()");
        // if(patternType.equals("expression"))
        //     return boolStore.getSizeHint();
        // else
        //     throw new IllegalStateException("Pattern type is " + patternType + ", yet someone called getSizeHint()");
    }

}
