## Developing P4Query applications

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
