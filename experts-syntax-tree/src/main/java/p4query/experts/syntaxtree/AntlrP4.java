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
package p4query.experts.syntaxtree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.anarres.cpp.CppReader;
import org.anarres.cpp.Preprocessor;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.codejargon.feather.Provides;

import p4query.experts.syntaxtree.p4.P4Lexer;
import p4query.experts.syntaxtree.p4.P4Parser;
import p4query.ontology.Status;
import p4query.ontology.analyses.SyntaxTree;
import p4query.ontology.providers.P4FileProvider.InputP4File;
import p4query.ontology.providers.P4FileProvider.P4Include;

public class AntlrP4 {

    @Provides
    @Singleton
    @SyntaxTree
    public Status analyse(GraphTraversalSource g, @InputP4File File inputP4, @P4Include List<String> includeDirs) throws IOException {
    
        long startTime = System.currentTimeMillis();
        System.out.println(SyntaxTree.class.getSimpleName() +" started.");

// // Antlr4 P4 parser generation is now automatically managed by Maven. 
// // In case of emergency, this can also generate P4Lexer class and P4Parser class along with the P4BaseVisitor class:
//      org.antlr.v4.Tool.main(new String[]{"-visitor", "-o", "hmm/src/main/java/hmm/p4", "-package", "hmm.p4", "P4.g4"});

//  // To parse without resolving includes:
//        CharStream stream = CharStreams.fromFileName(BASIC_P4);
//        P4Lexer lexer  = new P4Lexer(stream);   

        // Using C preprocessor to resolve includes. 
        // JCPP-Antlr integration from here: https://stackoverflow.com/a/25358397
        // Note that includes are huge, they slow down everything, and many things can be analysed without them.
        Preprocessor pp = new Preprocessor(inputP4);

        pp.setSystemIncludePath(includeDirs);
        
        P4Lexer lexer = new P4Lexer(CharStreams.fromReader(new CppReader(pp)));

        System.out.println("Includes found: " + pp.getIncludes());
        pp.close();
        TokenStream tokenStream = new CommonTokenStream(lexer);

        P4Parser parser = new P4Parser(tokenStream);
        
        ParseTree tree = parser.start();
//        displayNativeAntlrTree(parser, tree);
//        antlrParseTreeToXML(tree);

        TinkerGraphParseTree.fromParseTree(g, tree, lexer.getVocabulary(), parser.getRuleNames());

        long stopTime = System.currentTimeMillis();
        System.out.println(String.format("%s complete. Time used: %s ms.", SyntaxTree.class.getSimpleName() , stopTime - startTime));

        return new Status();
    }

    private static void displayNativeAntlrTree(P4Parser parser, ParseTree tree) {
        //show AST in GUI
        TreeViewer viewer = new TreeViewer( Arrays.asList(parser.getRuleNames()),tree);
        viewer.open();

        //show AST in console (LISP)
        System.out.println(tree.toStringTree(parser));
    }

    private static void antlrParseTreeToXML(ParseTree tree)  {
        
        try {
            XMLParseTree.toFile(XMLParseTree.fromParseTree(tree), "p4-antlr.xml", true);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

// // stress test case generators 
//    public static void stressTest(int n) {
//        try {
//            PrintStream out = new PrintStream("/tmp/filename.txt");
//        
//            System.out.println("");
//            System.out.println("// 511");
//            f(511);        
//
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        System.out.println();  
//        System.exit(0);
//    }

//    public static void f(int n) {
//        if (n == 1) {
//            System.out.print("{ ipv4_lpm.apply(); }");
//        } else if (n > 1) {
//            System.out.print("{ ipv4_lpm.apply(); ");
//            f(n - 1);
//            System.out.print("}");
//        }
//    }
//
//    public static void g(int n, PrintStream out) {
//
//            if(n == 0){
//            out.print("ipv4_lpm.apply();");
//            }
//            else if(n > 0){
//            out.print("if(hdr.ipv4.isValid()){ipv4_lpm.apply();"); 
//            g(n - 1, out);
//            out.print("}else{ipv4_lpm.apply();"); 
//            g(n - 1, out);
//            out.print("}"); 
//            }
//            out.close();
//    }
//    public static void h(int n){
//        for (int i = 0; i < n; i++) {
//          System.out.print("{ ipv4_lpm.apply(); }");
//        }
//    }
}
