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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.externs.implem.FillTablesImpl;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Const;
import p4query.applications.smc.lir.iset.GetField;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.GlobalAddress;
import p4query.ontology.Dom;

// In P4, normal calls are returning simple values, but table applications return composites (see 13.2.2. Match-action unit invocation). For simplicity, I just return a pointer to the struct that the table manipulates, based on the selected field.
public class InlineTableApplication implements Expression {

    private ProcedureCallExpression tableCall;
    private String fieldSelector;
    private CompilerState state;

    InlineTableApplication(CompilerState state, Vertex src) throws UnableToParseException {
        this.state = state;
        GraphTraversalSource g = state.getG();

        this.fieldSelector = (String)
            g.V(src).outE(Dom.SYN).has(Dom.Syn.E.RULE, "name").inV()
             .repeat(__.out(Dom.SYN))
             .until(__.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
             .values("value")
             .next();

        Vertex expr = 
            g.V(src)
             .outE(Dom.SYN).has(Dom.Syn.E.RULE, "expression").inV()
             .next();

        this.tableCall = new ProcedureCallExpression(state, expr);

    }

    @Override
    public String toP4Syntax() {
        return tableCall.toP4Syntax() + "." + fieldSelector;
    }

    @Override
    public StorageReference getStorageReference() {
      throw new IllegalStateException("InlineTableApplication has no local storage, yet someone called getStorageReference().");
    }

    @Override
    public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
      List<StackInstruction> insts = new LinkedList<>();

      insts.add(new Comment(this.toP4Syntax()));

      insts.addAll(tableCall.compileToLIR(local, global));

      String hitId = tableCall.getCallee().getName() + ".hit";
      Integer hitAddr = global.lookupSegmentByName(hitId).getAddress();
      insts.add(new Const(new GlobalAddress(hitAddr, hitId)));
      
      insts.add(new Comment("end of " + this.toP4Syntax()));

      return insts;
    }

    @Override
    public IRType getTypeHint() {
        throw new IllegalStateException("InlineTableApplication has no size hint, yet someone called getSizeHint().");
    }
    
}
