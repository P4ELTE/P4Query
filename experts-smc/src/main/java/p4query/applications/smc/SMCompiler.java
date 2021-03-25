package p4query.applications.smc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.Program;
import p4query.applications.smc.lir.StackProgram;
import p4query.ontology.Dom;
import p4query.ontology.Status;
import p4query.ontology.analyses.CallGraph;
import p4query.ontology.analyses.CallSites;
import p4query.ontology.analyses.ControlFlow;
import p4query.ontology.providers.AppUI;
import p4query.ontology.providers.Application;
import p4query.ontology.providers.CLIArgs;
import p4query.ontology.providers.P4FileProvider.InputP4File;

// NOTE: This a proof-of-concept compiler for a stack machine language similar to Java bytecode. 
//       The language implementation itself will be published later.
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
//    @CallGraph Status notUsed; // used for argument and parameter discovery

    @Inject
    @InputP4File 
    private File inputP4;

    @Inject
    @CLIArgs 
    private AppUI args; 
    // NOTE: coding guideline: queries that may fail should be surrounded with try-catch, and rethrow an explanation 

    @Override
    public Status run() throws Exception{

//        long startTimeApp = System.currentTimeMillis();

        Program p = new Program(g);
        StackProgram sp = p.compileToLIR();

        String origOutputPath = Paths.get(ui.outputDir, "basic.p4").toString();
        String asmOutputPath = Paths.get(ui.outputDir, "basic.p4.asm.c").toString();

        Files.copy(Paths.get(inputP4.getAbsolutePath()), Paths.get(origOutputPath), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Input P4 file copied to " + origOutputPath);

        PrintStream os = new PrintStream(new FileOutputStream(asmOutputPath, false));
        sp.toHumanReadable(os);
        os.close();
        System.out.println("Human-readable ASM written to " + asmOutputPath);

//        long stopTimeApp = System.currentTimeMillis();
//        System.out.println(String.format("Application complete. Time used: %s ms.", stopTimeApp - startTimeApp));
        return new Status();
    }

}
