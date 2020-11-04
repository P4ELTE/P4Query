# P4 code analysis

Copyright &copy; 2020 ELTE IK

## Usage

It should be possible to import the repository root as a folder in VSCode or [VSCodium](https://vscodium.com/).

Target is Java 8. 

Recommended extensions for VSCode: Java Extension Pack (Microsoft), Language Support for Java (Red Hat), Maven for Java (Microsoft).  

For visualisation, you will need [Graphviz](https://graphviz.org/download/) (tested with 2.38.0).

## Gremlin

The P4 knowledge graph resides in a TinkerPop graph database. Database queries for TinkerPop are graph traversals written in the Gremlin programming language. Basically, Gremlin is a functional programming language, and programs are descriptions of the graph paths that you want to traverse, and the side-effects you want to set off during your traversal (somewhat similar to XQuery).

- Tutorial: https://tinkerpop.apache.org/docs/current/#tutorials
- Cookbook: http://kelvinlawrence.net/book/PracticalGremlin.html
- Reference documentation: https://tinkerpop.apache.org/docs/current/reference/#graph-traversal-steps
- Gremlin white paper: https://arxiv.org/pdf/1508.03843.pdf
- Javadoc:
  * http://tinkerpop.apache.org/javadocs/3.4.8/full/org/apache/tinkerpop/gremlin/structure/package-summary.html
  * http://tinkerpop.apache.org/javadocs/3.4.8/full/org/apache/tinkerpop/gremlin/process/traversal/package-summary.html

## Goals

- **Open platform:** The P4 code analysis platform is intended as an open support structure for all implementations of optimising compilers, IDEs (incl. LSP-compliant ones), code comprehension dashboards, formal verification tools, etc. targeting the P4 programming language.
- **Data-driven:** Don't work on the code, work on the data! It doesn't matter *how* a static analysis procedure delivers its results, as long as these results are correct. What matters is *what* information does it need, and *what* information does it provide. This information is easily visualised, verified, and built upon.
- **Test-driven:** Clear input requirements and output guarantees are easy to turn into pre- and postconditions for testing and verification. A strong testing framework aids development both in validating new analysers and integrating validated analysers in the existing code base (CI/CD). 
- **Knowledge-based:** Static analysis extracts new information from program code without executing the program code. In other words, any static analysis procedure is an intelligent expert inferring new facts from existing facts. At the end of the chain there is knowledge: useful information that can be easily processed by human experts (developers, engineers, and tech managers) and application-specific software.
- **Graph-based:** Most data structures utilised in static analysis are trees and DAGs. Then, it makes sense to store all facts in one big, uniform, graph-shaped universe, where everything is connected to everything, and every information is just one link away. This universe is founded upon an efficient and multifaceted infrastructure provided by a state of the art graph database.
- **Distributed:** Where and when static analysis queries are executed is constrained as little as possible. This enables concurrent query execution that, in turn, boosts availability and efficiency at the same time.


## Current code structure 

The code base is modular, and all modules are in the root directory (a requirement by Maven for multi-module projects). In theory each module could be developed and stored in a separate git repository, and then imported as submodule in the main project repository. 

- `blackboard`: Knowledge graph (Gremlin Server) and possibly further data access layers. 
- `broker`: Component for coordinating access to the `blackboard` by various actors. Currently implemented as a DAG in Ant.
- `experts-...`: Experts (actors, knowledge sources) that know how to derive new information from existing knowledge. When these are invoked by the `broker`, they connect to the `blackboard`, analyse its content, and add new information.
- `ontology`: Metadata shared between actors that prescribes/describes what kind of data they should put into the database.

## Current `broker` implementation

In the `broker` module, there is `src/main/resources/broker.xml`. This is a repurposed Ant build file that declares the data dependencies between various information types that one or more experts will deliver.

**Example:** The following `broker.xml` rules declares that any analysis that delivers the `symbol-table` information, will be preceded by the analysis that delivers the `syntax-tree`. In other words, the `symbol-table` expert can be assured that when it invoked, the `syntax-tree` information is already in the knowledge graph and can be used.

```xml
    <import> <javaresource name="syntax-tree.xml"> <classpath/> </javaresource> </import> 
    <import> <javaresource name="symbol-table.xml"> <classpath/> </javaresource> </import>
    
    <extension-point name="syntax-tree"/>
    <extension-point name="symbol-table" depends="syntax-tree"/>
```

Note that `broker.xml` does not prescribe *who* will perform the analysis: it prescribes *what* information types must be inferred. It also prescribes the dependencies between information types, and unknowingly invokes various analysers using whatever `syntax-tree.xml` and `symbol-table.xml` someone put on the classpath.


The target that extends `symbol-table` have to reside in a file named `symbol-table.xml` and copied on the classpath. This is as easy as putting the file in the `src/main/resources` folder of the module performing the analysis (because in this case Maven will copy it on the classpath).  

An example for one such target can look like the following, invoking a Java class with the command-line arguments required to connect to the Gremlin Server:

```xml
    <target name="symbol-table-implem" extensionOf="symbol-table">
        <java classname="p4analyser.experts.symboltable.SymbolTable">
            <arg value="${host}"/>
            <arg value="${port}"/>
            <arg value="${remoteTraversalSourceName}"/>
        </java>
        
        <echo message="OK"></echo>
    </target>
```

## How to create a new `expert` project

1. Create a new Maven project in the root folder. In VSCode there is a button for this (*Create Maven project*), and it will automatically perform the following steps: 
    a. Update the `modules` element in the root `pom.xml` with the name of the new module.
    b. Include the `parent` element in the `pom.xml` of the new module
2. Build the new module for the first time. In VSCode, you can do this by restarting VSCode
3. Choose (or create) a goal in `broker.xml` that you will implement. Create the corresponding `.xml` in `src/main/resources` and extend your chosen goal with a new target. This target should invoke your implementation that will carry out the goal.
4. Add the new module as a `runtime` dependency for the `broker` module in the `pom.xml` of the `broker`. This will ensure that Maven will copy your `.xml` on the classpath of `broker` when `broker` is executed, and so it can be imported in `broker.xml`.





