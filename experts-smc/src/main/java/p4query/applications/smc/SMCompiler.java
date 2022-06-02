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
package p4query.applications.smc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import p4query.applications.smc.hir.Program;
import p4query.applications.smc.hir.externs.IUseCase;
import p4query.applications.smc.lir.StackProgram;
import p4query.ontology.Status;
import p4query.ontology.analyses.CallGraph;
import p4query.ontology.analyses.CallSites;
import p4query.ontology.analyses.ControlFlow;
import p4query.ontology.providers.AppUI;
import p4query.ontology.providers.Application;
import p4query.ontology.providers.CLIArgs;
import p4query.ontology.providers.P4FileProvider.InputP4File;

// NOTE: This a proof-of-concept compiler for a stack machine language similar to Java bytecode. 
//       Classes respresenting each instruction are in package p4query.applications.smc.lir.iset.


public class SMCompiler implements Application {
    private static class SMCUI extends AppUI {
        @Override
        public String getCommandName() {
            return "smc";
        }

        @Override
        public String[] getCommandNameAliases() {
            return new String[]{};
        }

        @Parameter(names = { "-o", "--output-dir"}, 
                    description = "<target directory where output files are generated>") 
        public String outputDir = System.getProperty("java.io.tmpdir");

        @Parameter(names = { "-u", "--use-case"}, 
                    description = "<Path to a .json describing a use case (packet distribution, table contents)>") 
        public String pathToUseCase = null;
    }


    SMCUI ui = new SMCUI();

    @Override
    public AppUI getUI() {
        return ui;
    }

    @Inject
    private GraphTraversalSource g;

    @Inject
    @ControlFlow 
    private Status cfg;

    @Inject
    @CallSites 
    private Status sites; // used for argument and parameter discovery

//    @Inject
//    @CallGraph Status notUsed; 

    @Inject
    @InputP4File 
    private File inputP4;

    @Inject
    @CLIArgs 
    private AppUI args; 
    // NOTE: coding guideline: queries that may fail should be surrounded with try-catch, and rethrow an explanation 
    

    @Override
    public Status run() throws Exception{
        // long startTimeApp = System.currentTimeMillis();

        String fileName = inputP4.getName();

        IUseCase useCase;
        if(ui.pathToUseCase == null){
            useCase = new BasicRouterBmv2UseCase();
        } else {
//            useCase = new BasicRouterBmv2UseCase(ui.nondet);
            useCase = UseCaseFromJSON.Factory.create(ui.pathToUseCase);
        }

        boolean nondet = false;
        String origOutputPath = Paths.get(ui.outputDir, fileName).toString();
        String asmOutputPath = Paths.get(ui.outputDir, fileName + ".asm.c").toString();
        String prismOutputPath = Paths.get(ui.outputDir,  fileName + ".prism").toString();

        compile(useCase, nondet, origOutputPath, asmOutputPath, prismOutputPath);

        nondet = true;
        origOutputPath = Paths.get(ui.outputDir, fileName).toString();
        asmOutputPath = Paths.get(ui.outputDir, fileName + "-nondet.asm.c").toString();
        prismOutputPath = Paths.get(ui.outputDir,  fileName + "-nondet.prism").toString();

        compile(useCase, nondet, origOutputPath, asmOutputPath, prismOutputPath);

//        long stopTimeApp = System.currentTimeMillis();
//        System.out.println(String.format("Application complete. Time used: %s ms.", stopTimeApp - startTimeApp));
        return new Status();
    }


    private void compile(IUseCase useCase,
                         boolean nondet,
                         String origOutputPath,
                         String asmOutputPath,
                         String prismOutputPath) throws IOException, FileNotFoundException {

        Program p = new Program(g, useCase, nondet);
        StackProgram sp = p.compileToLIR();

        Files.copy(Paths.get(inputP4.getAbsolutePath()), Paths.get(origOutputPath), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Input P4 file copied to " + origOutputPath);

        PrintStream os = new PrintStream(new FileOutputStream(asmOutputPath, false));
        sp.toHumanReadable(os);
        os.close();
        System.out.println("Human-readable ASM written to " + asmOutputPath);

        PrintStream os2 = new PrintStream(new FileOutputStream(prismOutputPath, false));
        sp.toPrism(os2);
        os2.close();
        System.out.println("PRISM written to " + prismOutputPath);
    }


}
