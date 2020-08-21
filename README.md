# P4 code analysis

Copyright &copy; 2020 ELTE IK

## Usage

It should be possible to import the repository root as a folder in VSCode. 

Target is Java 8. 

Recommended extensions for VSCode: Java Extension Pack (Microsoft), Language Support for Java (Red Hat), Maven for Java (Microsoft).  

## Current code structure (to be changed later)

`p4parser/src/main/java/parser`:

- `AnltrP4.java`: Main class. Creates syntax tree using ANTLR4 and uploads it to a [TinkerGraph](http://tinkerpop.apache.org/) knowledge graph using `./TinkerGraphParserTree.java`. 
- `Dom.java`: Domain hiearchies for the knowledge graph schema to support easy renaming of attributes and values.
- `SemanticAnalysis.java`, `ControlFlowAnalysis.java`: Graph query based analyses. Results are added back into the knowledge graph. 
- `p4` folder: P4 parser generated from `/P4.g4` using ANTLR4.
- `GraphUtils.java`: Selecting subgraphs based on labels, printing graphs using `/graphml2dot.xsl`.
- `GremlinUtils.java`: Common queries related to the knowledge graph schema.
