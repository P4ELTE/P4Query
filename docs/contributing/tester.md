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



