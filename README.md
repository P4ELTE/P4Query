Copyright 2020, Eötvös Loránd University.
All rights reserved.

# P4 code analysis

## Description

`p4analyser` is a static code analysis framework for the P4 language. We intend `p4analyser` to be a knowledge base or infrastructure that can host various applications, such as compilers, code comprehension tools, IDEs, and formal verification tools.

Implemented features (applications):

- Visualization 
- Verification of various safety-properties 

Implemented features (infrastructure):

- Syntax tree (based on P4 specs)
- Abstract syntax tree 
- Symbol table
- Call analysis
- Control flow analysis
- All information is stored in a knowledge graph (a graph database with Gremlin API).
- Several other features (such as program dependency and data flow analysis) are planned.



## Installation

### Current release

### Dependencies

## Usage

`p4analyser` has two kinds of users: 

- **End-users** only use existing applications. 
- **Application developers** mainly use the infrastructure to create new applications, but occasionaly they also act as end-users and use existing applications that help in development (e.g. they visualise the P4 knowledge graph, or experiment with graph queries). 

In this section, we address end-users. Application developers working with the infrastructure should explore the [Contributors](TODO) section. 

`p4analyser` has a layered architecture. End-users only interact with applications. Applications in turn interact with knowledge graph in the infrastructure layer.

![Figure: Layers](guides/figures/layers.png){ width=40% }


All built-in applications of `p4analyser` can be launched from the command line interface (CLI). For example, assuming you store a P4 program at path `$EXAMPLES/basic.p4`{.sh}, and want launch the graph query REPL, just navigate into the directory where you store `p4analyser-x-y-z.jar`{.sh} and fire up `p4analyser` like this in your terminal:

```sh
$ p4analyser repl $EXAMPLES/basic.p4
```

By passing `repl` as the first argument, you tell `p4analyser` to launch the application named `repl`. The second argument -- a file name -- will be passed to the REPL application. In turn, the REPL will load the file into the knowledge graph (unless it is loaded already), and start accepting your user inputs (valid queries in Gremlin-Java). For more information on using REPL, see [REPL](TODO).


Another example is graph visualization, which was implemented as another application:

```sh
$ p4analyser draw $EXAMPLES/basic.p4 -A SymbolTable ControlFlow
```

Again the first argument tells `p4analyser` to launch the visualizer application with the following arguments. Specifically, the visualizer will load the file into knowledge graph (unless it is loaded already), and then show you a subgraph that contains only the symbol table and control flow edges. For more information on graph visualization, see [Visualizer](TODO).


Finally, to formally verify that `$EXAMPLES/basic.p4`{.sh} is bug-free (at least with respect some bugs), simply run:

```sh
$ p4analyser verify $EXAMPLES/basic.p4 
```

For more information on P4 formal verification, see [Verification](TODO).


### Persistent database

Syntactic and semantic analysis can take a long time, so it is faster if they do not have to be performed every time an application is started. 

To persist the database, use the `--store <location>` option with the location where the database (or databases if you invoke the tool on multiple files) will be stored. If you run the same application twice on the same location, the second execution will take much less time since the database is already complete. Moreover, if you run a different application, only those analyses will be performed whose results are not yet in the database.


## Contributing

It should be possible to import the repository root as a folder in VSCode or [VSCodium](https://vscodium.com/).

Target is Java 8. (See section below on how to setup VSCode for Java 8.)

Recommended extensions for VSCode: Java Extension Pack (Microsoft), Language Support for Java (Red Hat), Maven for Java (Microsoft).  

For visualisation, you will need [Graphviz](https://graphviz.org/download/) (tested with 2.38.0).

For running the project using VSCode, you can set arguments for launching `controller.Main` (current file) inside `launch.json`.

For running the project outside VSCode, you have to add arguments inside `exec-maven-plugin` plugin in the `pom.xml` inside the `controller` module, e.g.:

```xml
<mainClass>controller.Main</mainClass>
<arguments>
  <argument>draw</argument>
  <argument>--help</argument>
</arguments>
```

Then, you can run the project with the command:

```sh
$ mvn exec:java
```

Or with less Maven noise: 

```sh
$ mvn exec:java -pl controller
```

### How to make jar
It needs a new plugin with which we can make the jar. You need to add this to the main `pom.xml`, after the compiler plugin.

```
 <plugin>
	<artifactId>maven-assembly-plugin</artifactId>
    <configuration>
	  <archive>
	    <manifest>
		  <mainClass>p4analyser.broker.App</mainClass>
	    </manifest>
	  </archive>
	  <descriptorRefs>
	    <descriptorRef>jar-with-dependencies</descriptorRef>
	  </descriptorRefs>
	  <finalName>p4analyser</finalName>
    </configuration>
  </plugin>
```

Run the following command in the (VSCode) Terminal:

```sh
$ mvn clean compile assembly:single
```

If the running is succesfull, you will find a `p4analyser-jar-with-dependencies.jar` in the `./broker/target` folder.

From the terminal you can run this jar with or without an argument:

```sh
$ java -jar p4analyser-jar-with-dependencies.jar
 or
$ java -jar p4analyser-jar-with-dependencies.jar "verify"
```

**Now, it makes the jar, and I can start it, but I think there is a problem with the usage of the File class from starting the jar.**

**In Windows I got this:** java.lang.IllegalStateException: Error parsing Gremlin client file at file:/D:/Egyetem/Doktori/tanszeki/ELTE-P4-Analyzer/broker/target/p4analyser-jar-with-dependencies.jar!/conf/remote-graph.properties 

### VSCode an Java 8

The VSCode Java plugin requires Java 11 or newer, but projects can still be built using Java 8. 

Press `Ctrl+,` and in the "User" tab, search for `java.configuration.runtimes`. Click "Edit in settings.json", and add the following (correct the `path` field to your local setup): 

```
"java.configuration.runtimes": [{
  "name": "JavaSE-1.8",
  "path": "/usr/lib/jvm/java-8-openjdk-amd64",
  "default": true
}],
```

After that issue an `mvn clean` and clean the workspace.

### Gremlin

The P4 knowledge graph resides in a TinkerPop graph database. Database queries for TinkerPop are graph traversals written in the Gremlin programming language. Basically, Gremlin is a functional programming language, and programs are descriptions of the graph paths that you want to traverse, and the side-effects you want to set off during your traversal (somewhat similar to XQuery).

- Tutorial: https://tinkerpop.apache.org/docs/current/#tutorials
- Cookbook: http://kelvinlawrence.net/book/PracticalGremlin.html
- Reference documentation: https://tinkerpop.apache.org/docs/current/reference/#graph-traversal-steps
- Gremlin white paper: https://arxiv.org/pdf/1508.03843.pdf
- Javadoc:
  * http://tinkerpop.apache.org/javadocs/3.4.8/full/org/apache/tinkerpop/gremlin/structure/package-summary.html
  * http://tinkerpop.apache.org/javadocs/3.4.8/full/org/apache/tinkerpop/gremlin/process/traversal/package-summary.html

### Goals

- **Open platform:** The P4 code analysis platform is intended as an open support structure for all implementations of optimising compilers, IDEs (incl. LSP-compliant ones), code comprehension dashboards, formal verification tools, etc. targeting the P4 programming language.
- **Data-driven:** Don't work on the code, work on the data! It doesn't matter *how* a static analysis procedure delivers its results, as long as these results are correct. What matters is *what* information does it need, and *what* information does it provide. This information is easily visualised, verified, and built upon.
- **Test-driven:** Clear input requirements and output guarantees are easy to turn into pre- and postconditions for testing and verification. A strong testing framework aids development both in validating new analysers and integrating validated analysers in the existing code base (CI/CD). 
- **Knowledge-based:** Static analysis extracts new information from program code without executing the program code. In other words, any static analysis procedure is an intelligent expert inferring new facts from existing facts. At the end of the chain there is knowledge: useful information that can be easily processed by human experts (developers, engineers, and tech managers) and application-specific software.
- **Graph-based:** Most data structures utilised in static analysis are trees and DAGs. Then, it makes sense to store all facts in one big, uniform, graph-shaped universe, where everything is connected to everything, and every information is just one link away. This universe is founded upon an efficient and multifaceted infrastructure provided by a state of the art graph database.
- **Distributed:** Where and when static analysis queries are executed is constrained as little as possible. This enables concurrent query execution that, in turn, boosts availability and efficiency at the same time.

### Planned architecture

![Figure: Planned architecture](guides/figures/component.png){ width=50% }


### Current code structure 

The code base is modular, and all modules are in the root directory (a requirement by Maven for multi-module projects). In theory each module could be developed and stored in a separate git repository, and then imported as submodule in the main project repository. 

- `blackboard`: Knowledge graph (Gremlin Server) and possibly further data access layers. 
- `broker`: Component for coordinating access to the `blackboard` by various actors. Currently implemented as a DAG in Ant.
- `experts-...`: Experts (actors, knowledge sources) that know how to derive new information from existing knowledge. When these are invoked by the `broker`, they connect to the `blackboard`, analyse its content, and add new information.
- `application-...`:  TODO
- `ontology`: Metadata shared between actors that prescribes/describes what kind of data they should put into the database.

### Tips

Use the visualizer module to explore the graph. If you introduce a new analysis, modify the visualization module to visualize the results of your analysis as well.

Take advantage of database persistance to speed up development (`-s` option). You will need the `--readonly` option as well so your own modifications will not be persisted, and you will be able to always experiment with the same database.

### How to create a new module?

1. Create a new Maven project in the root folder. In VSCode there is a button for this (*Create Maven project*), and it will automatically perform the following steps: 
    a. Update the `modules` element in the root `pom.xml` with the name of the new module.
    b. Include the `parent` element in the `pom.xml` of the new module
2. Build your new module for the first time. In VSCode, you can do this by restarting VSCode
3. Add your new module as a `runtime` dependency for the `broker` module in the `pom.xml` of the `broker`. This ensures that Java will find your classes when `broker` is executed.



### How to implement an application?

Applications will be accessed by users. Accordingly, all applications must define a command line user interface. This is done in a class that implements `AppUI`. An instance of this class will be passed to the `broker`, who will use the `JCommander` library to fill in the missing fields annotated with `@Parameter`, based on the user arguments. `AppUI` objects must declare a unique name for the application: the user will invoke the application using this name, the `broker` will also use this name to find which application must be initialized.

   - Example:
   
     ```java    
     package p4analyser.applications;

     import com.beust.jcommander.Parameter;
     import com.beust.jcommander.Parameters;

     import p4analyser.ontology.providers.AppUI;

     @Parameters(commandDescription = "Launch my application")
     public class MyAppUI extends AppUI {

         @Override
         public String getCommandName() { return "myapp"; }

         @Override
         public String[] getCommandNameAliases() {
             return new String[]{"myApp", "MyApp", "my", "ma"};
         }

         @Parameter(names = { "-s", "--syntax-tree" },
                    description = "Triggers syntax tree analysis")
         private Boolean synTree;

     }
     ```

   - The `AppUI` abstract class requires the `MyAppUI` class to implement the `getCommandName()` method, so that it returns the unique name of the application. 
   - In addition, `MyAppUI` annotates its custom `synTree` argument with `@Parameter`: if the user states the `-s` option on the command line, the `broker` will set `synTree` to `true`, otherwise it will be left on `false`.
   - Note that there are a number few user argument fields that `AppUI` declares which are universal for all applications. For example the path to the input P4 file is requested inside `AppUI`, and it is mainly used by `broker` to load that P4 file, but you may access it as well. 


No, that the interface is ready, we can implement the application logic. Note that the main class of the actual Java software is in the `broker`. You implement your application as a module, and the `broker` will discover it automatically. 

1. First, create a new module (see above), and add the `ontology` module as a dependency. 
2. Create a class in `p4analyser.applications` that will implement your application logic and is capable of providing an instance of this class when requested by the dependency injector (DI) in `broker`. Make sure you get the package right, since this is how `broker` will find your application. The DI will provide your class with everything it needs. Your implementation class needs to implement the `Application` interface, because `broker` will use this to ask for your user interface description.

    - Example:
    
      ```java    
      package p4analyser.applications;

      import com.beust.jcommander.Parameter;
      import com.beust.jcommander.Parameters;

      import org.codejargon.feather.Provides;
      import javax.inject.Inject;
      import javax.inject.Provider;
      import javax.inject.Singleton;

      import p4analyser.ontology.providers.Application;
      import p4analyser.ontology.providers.AppUI;
      import p4analyser.ontology.providers.P4FileProvider.InputP4File;
      import p4analyser.ontology.analyses.SyntaxTree;
      import p4analyser.ontology.Status;

      public class MyApp implements Application {

        // User interface

        private final MyAppUI ui = new MyAppUI();

        @Override
        public AppUI getUI(){
            return ui;
        }

        // Business logic

        @Inject
        private GraphTraversalSource g; 

        @Inject
        @InputP4File 
        private File file;

        @Inject
        @SyntaxTree 
        private Provider<Status> ensureSt;

        @Override
        public Status run(){
            if(ui.synTree)
              ensureSt.get();
            
            System.out.println(g.V().count().next());

            System.out.println("Done.");

            return new Status();
        }

      }
      ```

    - The `Application` interface requires this class to implement `getUI()`, so that it returns a reference to the command line interface of your application. The `broker` will use JCommander to fill in the `@Parameter` annotated arguments of this object by the time your application is started. 
    - The `Application` interface also requires this class to implement the the application logic inside `run()`. You have to state all your dependencies with `@Inject` annotated fields. All these dependencies will be satisfied by the `broker`. Usually, you depend on the knowledge graph `GraphTraversalSource`, and a certain number of analyses performed on the knowledge graph, but in special cases you may need direct access to the raw P4 file as well.
      * Special dependency names such as `@InputP4File` are defined in the classes of `ontology`. It's good to get to know this package and subpackages, to see what you can use. 
      * By now, the `broker` also filled the `@Parameter` fields of the object you return in `getUI()` with the user arguments, so you use this as well.
      * It may happen you do not want to initialize all your dependencies in all cases (e.g. you may only want to run syntax tree analysis, if the user requests it). In these cases, you can request a `Provider` instance that will only initialize the dependency if/when you call its `get()` method. 
  
3. Run the `broker` with the arguments you specified in your interface (e.g. in `MyUICommand`).

    ```sh
    $ p4analyser myapp --syntax-tree
    Done.
    ```


### How to implement an analyser?

#### Declaring the analysis

1. First, if the analysis you plan to implement was not declared before, you need to declare the analysis in `ontology.analyses`. Make sure you get the package right, since this is how `broker` will find the analysis. In your implementation you will use this to tell others that they can depend on this analysis in their own analysers.

      - Example:

        ```java
        package p4analyser.ontology.analyses;

        import java.lang.annotation.Retention;
        import java.lang.annotation.RetentionPolicy;

        import javax.inject.Qualifier;

        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface MySpecialAnalysis  {
        }
        ```

      - You will use this annotation in dependency injection. Specifically, you will use this to communicate to others that your analyser is capable of providing a `@MySpecialAnalysis` token. This token signifyies that your analysis has been completed. Others will claim dependency on this token, but they actually expect you to modify the knowledge graph according to the requirements of this analysis.
      - Additionally, `broker` will use this annotation to discover your modul by looking up which class has a method annotated with this annotation.


2. Then, you create the tests to completely define the requirements that your analysis satisfies. It may also be a good idea to extend the `experts-visualizer` application for your `@MySpecialAnalysis` analysis, so that you can actually see the results as you progress with your work.


#### Defining the analyser

Note that the main class of the actual Java software is in the `broker`. You implement your analysis as a module, and the `broker` will discover it automatically. 

1. First, create a new module (see above), and add the `ontology` module as a dependency. 
2. Create a class in `p4analyser.experts` that implements the selected analysis. Make sure you get the package right, since this is how `broker` will find the analyser. The dependency injector (DI) in `broker` will provide your class with everything it needs. 

    - Example:
    
      ```java    
      package p4analyser.experts;

      import org.codejargon.feather.Provides;
      import javax.inject.Singleton;

      import p4analyser.ontology.providers.P4FileProvider.InputP4File;
      import p4analyser.ontology.analyses.SyntaxTree;
      import p4analyser.ontology.analyses.MySpecialAnalysis;
      import p4analyser.ontology.Status;

      public class MySpecialAnalysisImpl {
        @Provides
        @Singleton
        @MySpecialAnalysis
        public Status analyse(GraphTraversalSource g, 
                            @SyntaxTree Provider<Status> ensureSt, 
                            @CLIArgs AppUI args, 
                            @InputP4File File inputP4){
          if(g.V().count().next() == 0)
            ensureSt.get();

          System.out.println(g.V().count().next());

          System.out.println("Done.");
          return new Status();
        }
      }
      ```

    - Note that the method `analyse` has a `@Provides` annotation. This tells the DI that `MySpecialAnalysisImpl` is capable of providing the `@MySpecialAnalysis` analysis on the knowledge graph. All parameters are injected by the `broker`. Usually, you depend on the knowledge graph `GraphTraversalSource`, and a certain number of analyses performed on the knowledge graph, but in special cases you may need direct access to the raw P4 file as well.
      * By convention, applications and analysers should always return `Status` type.
      * Special dependency names such as `@InputP4File` or `@CLIArgs` are defined in `ontology`. It's good to get to know this package and subpackages, to see what you can use. In this case, `@InputP4File` is a reference to the raw P4 file being processed (usually not needed), and `@CLIArgs` is an object storing the user provided command line arguments (usually not needed).
      * It may happen you do not want to initialize all your dependencies in all cases (e.g. you may only want to run syntax tree analysis, if the user requests it). In these cases, you can request a `Provider` instance that will only initialize the dependency if/when you call its `get()` method. 

3. Try it by running an application that depends on your `@MySpecialAnalysis` analysis. It may be a good idea to extend the `experts-visualizer` application, so that you can see the results.



### Experience report: Dependency injection vs. Ant DAG 

- Ant is defined in XML, and this got in the way of testing. Either `broker.xml` had to be duplicated for testing, or parts of the file had to be rewritten to select the right goals.
- It was difficult to customize fault tolerance with Ant (i.e. what can be done when an analyser fails, e.g. during testing).
- Ant had no built-in way to enforce implementation (extension) of an interface (target). If there was no extension, the target quietly succeeded, even though nothing happened.
- Ant made debugging more difficult with its deep stack traces.
- Ant dependency names were strings, and these had to be edited by hand. With DI, dependency names are Java classes. Typos will be catched by the compiler. IDE toolset can be used for renaming.
- With Ant the architecture was `broker -> Ant -> modules`. This limits modules, as their only input is what can be passed through Ant (i.e. command line arguments).
- Since Ant starts up new processes, it also limits what modules can output. Specifically, there were problems with JLine3 terminal not knowing where to output and crash, when it was run inside a module invoked by Ant.


## Credits

## License



