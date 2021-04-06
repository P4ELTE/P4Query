## Developing P4Query analysers

### How to create a new module?

1. Create a new Maven project in the root folder. In VSCode there is a button for this (*Create Maven project*), and it will automatically perform the following steps: 
    a. Update the `modules` element in the root `pom.xml` with the name of the new module.
    b. Include the `parent` element in the `pom.xml` of the new module
2. Build your new module for the first time. In VSCode, you can do this by restarting VSCode
3. Add your new module as a `runtime` dependency for the `broker` module in the `pom.xml` of the `broker`. This ensures that Java will find your classes when `broker` is executed.

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
