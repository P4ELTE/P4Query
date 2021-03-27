# Maintainers guide

## How to make jar
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

## Updating license header

:warning: **WARNING:** Automatically management of license headers requires unsupervised editing of most source files. Always issue these commands in a fresh clone, never on your actual working copy.

License headers are managed through Maven by [Mycila's licensing plugin](https://github.com/mycila/license-maven-plugin).


To add missing headers, execute from the project root: 

```
mvn license:format
```

The contents of the license header is read from [NOTICE.md](../NOTICE.md). 

To update headers, execute from the project root: 

```
mvn license:format
```

The licensing plugin is configured in the root `pom.xml`, here you can issue e.g. which files no exclude from license stamping.


## Experience report: Dependency injection vs. Ant DAG 

In an older version dependency resolution of analysers was implemented using ANT. This is a comparison of ANT vs. DI.

- Ant is defined in XML, and this got in the way of testing. Either `broker.xml` had to be duplicated for testing, or parts of the file had to be rewritten to select the right goals.
- It was difficult to customize fault tolerance with Ant (i.e. what can be done when an analyser fails, e.g. during testing).
- Ant had no built-in way to enforce implementation (extension) of an interface (target). If there was no extension, the target quietly succeeded, even though nothing happened.
- Ant made debugging more difficult with its deep stack traces.
- Ant dependency names were strings, and these had to be edited by hand. With DI, dependency names are Java classes. Typos will be catched by the compiler. IDE toolset can be used for renaming.
- With Ant the architecture was `broker -> Ant -> modules`. This limits modules, as their only input is what can be passed through Ant (i.e. command line arguments).
- Since Ant starts up new processes, it also limits what modules can output. Specifically, there were problems with JLine3 terminal not knowing where to output and crash, when it was run inside a module invoked by Ant.

