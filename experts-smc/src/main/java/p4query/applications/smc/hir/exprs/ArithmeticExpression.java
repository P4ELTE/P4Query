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
package p4query.applications.smc.hir.exprs;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Definition;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.IRType.SingletonFactory;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.Invoke;
import p4query.applications.smc.lir.iset.Pop;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Size;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.ontology.Dom;

public class ArithmeticExpression implements Expression {
    // TODO make this an enum
    private final String operator;
    private List<Expression> operands = new LinkedList<>();
    private SingletonFactory typeFactory;
    private StorageReference storage;
    private int sizeHint;

    ArithmeticExpression(GraphTraversalSource g, Vertex src, IRType.SingletonFactory typeFactory,
            Definition parentDef, int sizeHint) {
        this.typeFactory = typeFactory;

        try {
            this.operator = (String) g.V(src).outE(Dom.SYN)
                    .or(__.has(Dom.Syn.E.RULE, "MINUS"), __.has(Dom.Syn.E.RULE, "PLUS")).values(Dom.Syn.E.RULE).next();
        } catch (NoSuchElementException e) {
            System.err.println(g.V(src).elementMap().next());
            throw new IllegalArgumentException(
                    String.format("ArithmeticExpression cannot be initialized on vertex %s.", src));
        }

        List<Vertex> opds = g.V(src).outE(Dom.SYN).not(__.has(Dom.Syn.E.RULE, this.operator)).order()
                .by(Dom.Syn.E.ORD, Order.asc).inV().toList();

        for (Vertex opd : opds) {
            operands.add(Expression.Factory.create(g, opd, typeFactory, parentDef, sizeHint));
        }

        allocateStorageReference(parentDef, sizeHint);
        this.sizeHint = sizeHint;
    }

    public String getOperator() {
        return operator;
    }

    public List<Expression> getOperands() {
        return operands;
    }

    @Override
    public String toP4Syntax() {
        if (!this.getOperator().equals("MINUS")) {
            throw new IllegalArgumentException("Unknown arithmetic expression operator " + this.getOperator());
        }
        return this.getOperands().get(0).toP4Syntax() + " - " +  this.getOperands().get(1).toP4Syntax();
    }

    public void allocateStorageReference(Definition def, int sizeHint) {

        if (!this.getOperator().equals("MINUS")) {
            throw new IllegalArgumentException("Unknown arithmetic expression operator " + this.getOperator());
        }
        Expression left = this.getOperands().get(0);
        Expression right = this.getOperands().get(1);

        Integer leftSize = null;
        Integer rightSize = null;
        if (left instanceof P4StorageReference) {
            leftSize = ((P4StorageReference) left).getSizeHint();
        }
        if (right instanceof P4StorageReference) {
            rightSize = ((P4StorageReference) left).getSizeHint();
        }

        if (leftSize != null && rightSize != null && !leftSize.equals(rightSize)) {
            throw new IllegalStateException(
                    String.format("Left opd %s and right opd %s has different sizes", left, right));
        } else if (leftSize != null) {
            rightSize = leftSize;
        } else if (rightSize != null) {
            leftSize = rightSize;
        } else { // both null
            rightSize = sizeHint;
            leftSize = sizeHint;
        }

        IRType type = typeFactory.create("INTEGER_" + sizeHint, sizeHint, this);
        String localName = def.addTemporary(type);
        CustomStorageReference result = new CustomStorageReference("ArithmeticExpressionContext", localName, type,
                this);

        this.storage = result;
    }

    @Override
    public StorageReference getStorageReference() {
        return storage;
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {

        LinkedList<StackInstruction> insts = new LinkedList<>();

        if (!this.getOperator().equals("MINUS")) {
            throw new IllegalArgumentException("Unknown arithmetic expression operator " + this.getOperator());
        }
        Expression left = this.getOperands().get(0);
        Expression right = this.getOperands().get(1);

        insts.add(new Comment(this.toP4Syntax()));
        insts.addAll(left.compileToLIR(local, global));
        insts.addAll(right.compileToLIR(local, global));
        insts.addAll(this.storage.compileToLIR(local, global));
        insts.add(new Const(new Size(left.getSizeHint(), left.toP4Syntax())));
        insts.add(new Invoke(new UnresolvedNameLabel("stdlib","subtract",""), new Size(4, "left, right, target, length")));
        // every function has a return value, but subtract's is not interesting now
        insts.add(new Pop());

      // subtract works in-place, and leaves target address on the stack
      //  int addr0 = this.storage.findSegment(local, global).getAddress();
      //  LocalAddress addr = new LocalAddress(addr0, this.storage.toP4Syntax());
      //  insts.add(new Load(addr));

        return insts;

    }

    @Override
    public int getSizeHint() {
        return sizeHint;
    }

    
}
