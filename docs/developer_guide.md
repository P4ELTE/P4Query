Copyright 2020, Eötvös Loránd University.
All rights reserved.

# P4Query - Developer Guide

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
		  <mainClass>p4query.broker.App</mainClass>
	    </manifest>
	  </archive>
	  <descriptorRefs>
	    <descriptorRef>jar-with-dependencies</descriptorRef>
	  </descriptorRefs>
	  <finalName>p4query</finalName>
    </configuration>
  </plugin>
```

Run the following command in the (VSCode) Terminal:

```sh
$ mvn clean compile assembly:single
```

If the running is succesfull, you will find a `p4query-jar-with-dependencies.jar` in the `./broker/target` folder.

From the terminal you can run this jar with or without an argument:

```sh
$ java -jar p4query-jar-with-dependencies.jar
 or
$ java -jar p4query-jar-with-dependencies.jar "verify"
```

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

![Figure: Planned architecture](../guides/figures/component.png)


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
     package p4query.applications;

     import com.beust.jcommander.Parameter;
     import com.beust.jcommander.Parameters;

     import p4query.ontology.providers.AppUI;

     @Parameters(commandDescription = "Launch my application")
     public class MyAppUI extends AppUI {

         @Override
         public String getCommandName() { return "myapp"; }

         @Override
         public String[] getCommandNameAliases() {
             return new String[]{"myApp", "MyApp", "my", "ma"};
         }

         @Parameter(names = { "-st", "--syntax-tree" },
                    description = "Triggers syntax tree analysis")
         private Boolean synTree;

     }
     ```

   - The `AppUI` abstract class requires the `MyAppUI` class to implement the `getCommandName()` method, so that it returns the unique name of the application. 
   - In addition, `MyAppUI` annotates its custom `synTree` argument with `@Parameter`: if the user states the `-s` option on the command line, the `broker` will set `synTree` to `true`, otherwise it will be left on `false`.
   - Note that there are a number few user argument fields that `AppUI` declares which are universal for all applications. For example the path to the input P4 file is requested inside `AppUI`, and it is mainly used by `broker` to load that P4 file, but you may access it as well. 


No, that the interface is ready, we can implement the application logic. Note that the main class of the actual Java software is in the `broker`. You implement your application as a module, and the `broker` will discover it automatically. 

1. First, create a new module (see above), and add the `ontology` module as a dependency. 
2. Create a class in `p4query.applications` that will implement your application logic and is capable of providing an instance of this class when requested by the dependency injector (DI) in `broker`. Make sure you get the package right, since this is how `broker` will find your application. The DI will provide your class with everything it needs. Your implementation class needs to implement the `Application` interface, because `broker` will use this to ask for your user interface description.

    - Example:
    
      ```java    
      package p4query.applications;

      import com.beust.jcommander.Parameter;
      import com.beust.jcommander.Parameters;

      import org.codejargon.feather.Provides;
      import javax.inject.Inject;
      import javax.inject.Provider;
      import javax.inject.Singleton;

      import p4query.ontology.providers.Application;
      import p4query.ontology.providers.AppUI;
      import p4query.ontology.providers.P4FileProvider.InputP4File;
      import p4query.ontology.analyses.SyntaxTree;
      import p4query.ontology.Status;

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
    $ p4query myapp --syntax-tree
    Done.
    ```


### How to implement an analyser?

#### Declaring the analysis

1. First, if the analysis you plan to implement was not declared before, you need to declare the analysis in `ontology.analyses`. Make sure you get the package right, since this is how `broker` will find the analysis. In your implementation you will use this to tell others that they can depend on this analysis in their own analysers.

      - Example:

        ```java
        package p4query.ontology.analyses;

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
2. Create a class in `p4query.experts` that implements the selected analysis. Make sure you get the package right, since this is how `broker` will find the analyser. The dependency injector (DI) in `broker` will provide your class with everything it needs. 

    - Example:
    
      ```java    
      package p4query.experts;

      import org.codejargon.feather.Provides;
      import javax.inject.Singleton;

      import p4query.ontology.providers.P4FileProvider.InputP4File;
      import p4query.ontology.analyses.SyntaxTree;
      import p4query.ontology.analyses.MySpecialAnalysis;
      import p4query.ontology.Status;

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

## Tests

### Unit Tests
Main goals: to be fast, short, simple, and check if one method is working well.

In every expert we can create Test classes, which contain the unit tests of it. These tests use the simplest graph which can be used to check if the methods are really doing the function that we expect from them.

**Maven will recognize the classes of the unit tests, if their name starts or ends with the _Test_ string, or ends with the _Tests_ or _TestCase_ strings.
These files should be put to the `_your-expert-folder_/src/test/java/p4query/experts/_your-expert-name_ folder`.**


An example structure of these tests can be found in the `CallGraph` expert. In this example there is a separated class - Befores.java - which contains the graphs, which are used during the test cases. These graph can be easily defined by creating a new TinkerGraph and a GraphTraversalSource for it. After this, the vertices and edges can be defined, and this GraphTraversalSource instance can be given to the test.

### Integration Tests
Main goals: to check the analyses in a detailed way; to check the results in P4 programs.

#### Structure of the test folder

The place of these tests is in the `Broker` - in the [`ir\broker\src\test\java\p4query\broker\`](https://gitlab.inf.elte.hu/p4_labor/ir/-/tree/v1alpha/broker/src/test/java/p4query/broker) folder.

The folder contains 2 main folders: `suites`, `tests`.

[`tests`](https://gitlab.inf.elte.hu/p4_labor/ir/-/tree/v1alpha/broker/src/test/java/p4query/broker/tests) is separated into folders based on the P4 source file, which they use, and in that folder they are spearated into different classes which are based on the analysis which they check. For example if we would like to create tests for the `call graph` analysis of the `basic.p4` file then we can put it into the `basicP4` folder as a `BasicP4CallGraph` class.

`suites` is separated into two types - [`analysisSuites`](https://gitlab.inf.elte.hu/p4_labor/ir/-/tree/v1alpha/broker/src/test/java/p4query/broker/suites/analysisSuites) and [`resourceSuites`](https://gitlab.inf.elte.hu/p4_labor/ir/-/tree/v1alpha/broker/src/test/java/p4query/broker/suites/resourceSuites).

#### Suite classes

The tests are suited i.e. we created a classes, which groups the tests

Example suite class in `analysisSuites` folder:

  ```java
  package p4query.broker.suites.analysisSuites;

  import org.junit.runner.RunWith;
  import org.junit.runners.Suite;
  import org.junit.runners.Suite.SuiteClasses;

  import p4query.broker.tests.basicP4.BasicP4CallGraph;
  import p4query.broker.tests.basicTunnelP4.BasicTunnelP4CallGraph;
  import p4query.broker.tests.testP4.TP4CallGraph;

  @RunWith(Suite.class)
  @SuiteClasses({BasicP4CallGraph.class, TP4CallGraph.class, BasicTunnelP4CallGraph.class})
  public class CallGraphAIT {

  }
  ```
  
  We can define which test classes we would like to group into our new class with the `@SuiteClasses`. In this example the concrete class is empty, it only defines that which classes should be executed.

  Example suite class in `resourceSuites` folder:

  ```java
    package p4query.broker.suites.resourceSuites;

    import org.junit.ClassRule;
    import org.junit.runner.RunWith;
    import org.junit.runners.Suite;
    import org.junit.runners.Suite.SuiteClasses;

    import p4query.broker.P4Resource;
    import java.util.Arrays;
    import java.util.List;

    import p4query.broker.tests.basicP4.*;

    @RunWith(Suite.class)
    @SuiteClasses({BasicP4CallGraph.class, BasicP4ControlFlow.class})
    public class BasicP4RIT {

        private static String fileName = "basic.p4";
        private static List<String> analyses = Arrays.asList("ControlFlow", "CallGraph");

        @ClassRule
        public static P4Resource source = P4Resource.getP4Resource(fileName, analyses);

    }
  ```

The `resourceSuites` classes have a nonempty body in which we need to define which file it uses and which analyses have tests. The `@ClassRule` is needed for the optimal execution, because in this way it execute every necessary analysis once, and after it it checks the tests.

#### Possible executions of the tests

For the execution we use the default integration test runner in maven, but they are redefined a little bit in the main `pom.xml`.

**Execute all tests**

It executes all of the tests based on the P4 sources.  

Its command:
```sh
$ mvn verify -P integration-test-by-resources
```

This command executes the classes, which end with the `RIT` string. These classes are in the `suites/resourceSuites` folder

**Execute tests of one analysis**

It executes the tests based on an analysis.

Its command:
```sh
$ mvn verify -P integration-test-by-analyses -D analyse=analysisName
```
, where `analysisName` can be the id of the chosen analysis.


This command executes the `_analysisName_AIT` class, therefore it calls all of the given analysis for all of the P4 sources.


#### Create new test file



If you need to create a **new analysis test class for a P4 source**: 

1. Check if the AIT of the analysis exists. If not then create one (there are steps down below).
2. Check if the RIT of the P4 source exists. If not then create one (there are steps down below). 
3. Create a new test class in the proper folder `tests/{fileFolder}`. Use the _Sample for new test class_. 
4. Redefine the `fileName` and `analyses`, and of course the tests.
5. Add your class to the proper AIT Suite class, which is in the `suites/analysisSuites` folder.
6. Add your class to the proper RIT Suite class, which is in the `suites/resourceSuites` folder.

If **AIT does not exist - or you test a new analysis**:
1. Create the AIT class for this new source into the `suites/analysisSuites` folder. Use the _Sample for new AIT_ for it. Redefine the `fileName` and `analysis`.

If **RIT does not exists - or you use a new test resource**:

1. Create the new test folder into the `tests` which shows the source id or name.
2. Make sure the P4 source is in the `broker/src/main/resources` folder.
3. Create the RIT class for this new source into the `suites/resourceSuites` folder. Use the _Sample for new RIT_ for it. Redefine the `fileName` and `analysis`.


**Sample for new test class:**
```java
package p4query.broker.tests.basicP4;

import org.junit.Test;

import p4query.ontology.Dom;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.BeforeClass;
import java.util.Arrays;
import java.util.List;
import p4query.broker.P4Resource;


public class YourClassName {
    
    private static String fileName = "TODO";
    private static List<String> analyses = Arrays.asList("TODO");
    private static GraphTraversalSource g;

    @BeforeClass
    public static void preTest() {
        P4Resource source = P4Resource.getP4Resource(fileName, analyses);
        g = source.getGraphTravSource();
    }

    @Test
    public void test1() {
        //TODO
    }
}
```

**Sample for a new RIT:**
```java
package p4query.broker.suites.resourceSuites;

//Need to import all of the classes for the P4 source
// for example - import p4query.broker.tests.testP4.*;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import p4query.broker.P4Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(Suite.class)
@SuiteClasses({/*TODO - FirstSuitedClass.class, SecondSuitedClass.class*/})
public class YourClassNameRIT {

    private static String fileName = "TODO";
    private static List<String> analyses = Arrays.asList("TODO");

    @ClassRule
    public static P4Resource source = P4Resource.getP4Resource(fileName, analyses);

}

```

Sample for a new AIT:
```java
package p4query.broker.suites.analysisSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//Need to import the proper analyses test classes


@RunWith(Suite.class)
@SuiteClasses({/*TODO - FirstSuitedClass.class, SecondSuitedClass.class*/})
public class YourClassNameAIT {
}
```

## Credits

## License


