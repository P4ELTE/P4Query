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
package p4query.applications.smc.hir;

import java.util.LinkedList;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import p4query.applications.smc.hir.InstructionLayout.Builder;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.IRType.SingletonFactory;

public class CompilerState {
    private LinkedList<IRType> globals = new LinkedList<>();
    private LinkedList<Declaration> declarations = new LinkedList<>();
    private GraphTraversalSource g;
    private IRType.SingletonFactory typeFactory;
    private InstructionLayout.Builder instLayout = new InstructionLayout.Builder();
    private Declaration parentDef = null;

    public CompilerState(LinkedList<IRType> globals, LinkedList<Declaration> declarations, GraphTraversalSource g,
            SingletonFactory typeFactory, Builder instLayout) {
        this.globals = globals;
        this.declarations = declarations;
        this.g = g;
        this.typeFactory = typeFactory;
        this.instLayout = instLayout;
    }

    public CompilerState(CompilerState s){
        this.globals = new LinkedList<>(s.globals);
        this.declarations = new LinkedList<>(s.declarations);
        this.g = s.g;
        this.typeFactory = new IRType.SingletonFactory(s.typeFactory);
        this.instLayout = new InstructionLayout.Builder(s.instLayout);
        this.parentDef = s.parentDef;
    }

    // this should be called only by privilieged classes
    public void setParentDecl(Declaration parentDef) {
        this.parentDef = parentDef;
    }


    public Declaration getParentDecl() {
        checkParentDef();
        return parentDef;
    }


    private void checkParentDef() {
        if(parentDef == null){
            throw new IllegalStateException("Parent function definition is not set. Compiler state should only be accessed when processing a function body.");
        }
    }

    public LinkedList<IRType> getGlobals() {
        return globals;
    }


    public LinkedList<Declaration> getDeclarations() {
        return declarations;
    }

    public GraphTraversalSource getG() {
        return g;
    }

    public IRType.SingletonFactory getTypeFactory() {
        return typeFactory;
    }

    public InstructionLayout.Builder getInstLayout() {
        return instLayout;
    }
}
