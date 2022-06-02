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

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.Not;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.ontology.Dom;

public class UnaryExpression implements Expression {
    // TODO make this an enum
    private final String operator;
    private List<Expression> operands = new LinkedList<>();
    private StorageReference storage;
    private IRType typeHint;
    private CompilerState state;

    UnaryExpression(CompilerState state, Vertex src, IRType typeHint) throws UnableToParseException {
        this.typeHint = typeHint;
        this.state = state;

        GraphTraversalSource g = state.getG();

        try {
            this.operator = 
                (String) g.V(src).outE(Dom.SYN)
                          .has(Dom.Syn.E.RULE, "NOT") 
                           .values(Dom.Syn.E.RULE).next();
        } catch (NoSuchElementException e) {
            throw new UnableToParseException(ArithmeticException.class, src);
        }

        List<Vertex> opds = g.V(src).outE(Dom.SYN).not(__.has(Dom.Syn.E.RULE, this.operator)).order()
                .by(Dom.Syn.E.ORD, Order.asc).inV().toList();

        // Arithemtic operations work with operands of the same size.
        for (Vertex opd : opds) {
            operands.add(Expression.Factory.create(state, opd, typeHint));
        }

        allocateStorageReference(typeHint);
    }

    public String getOperator() {
        return operator;
    }

    public List<Expression> getOperands() {
        return operands;
    }

    @Override
    public String toP4Syntax() {
        if(this.getOperator().equals("NOT")){
            return "!" + this.getOperands().get(0).toP4Syntax();
        }
        throw new IllegalArgumentException("Unknown unary expression operator " + this.getOperator());
    }

    public void allocateStorageReference(IRType typeHint) {

        if (!this.getOperator().equals("NOT")) {
            throw new IllegalArgumentException("Unknown unary expression operator " + this.getOperator());
        }
        String globalName = state.getParentDecl().addTemporary(typeHint);
        CustomStorageReference result = new CustomStorageReference(state, "Unary expression", globalName, typeHint,
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

        if (!this.getOperator().equals("NOT")) {
            throw new IllegalArgumentException("Unknown unary expression operator " + this.getOperator());
        }
        Expression right = this.getOperands().get(0);

        insts.add(new Comment(this.toP4Syntax() + " (size: " + typeHint.getSize() + ")"));
        insts.addAll(right.compileToLIR(local, global));
        
        insts.add(new GetField());
       // insts.addAll(this.storage.compileToLIR(local, global));
        insts.add(new Not());
      // subtract works in-place, and leaves target address on the stack

        insts.add(new Comment("end of " + this.toP4Syntax() + " (size: " + typeHint.getSize() + ")"));

        return insts;

    }

    @Override
    public IRType getTypeHint() {
        return typeHint;
    }

    
}
