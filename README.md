# P4Query

## :warning: *Stability warning* :warning:

This is a pre-alpha proof-of-concept tool without extensive testing: expect bugs and incomplete coverage. 

## Description

`P4Query` is a static code analysis framework for the P4 language. We intend `P4Query` to be a knowledge base or infrastructure that can host various coding-related applications, such as compilers, code comprehension tools, IDEs, and formal verification tools.

Implemented features (applications):

- Visualization 
- Verification of various safety-properties 
- A proof-of-concept P4 compiler 

Implemented features (infrastructure):

- Syntax tree (based on P4 specs)
- Symbol table
- Call analysis
- Control flow analysis
- All information is stored in a knowledge graph (a graph database with Gremlin API).
- Several other features (such as program dependency and data flow analysis) are planned.

## Getting started

### Dependencies

You need Java 8 or higher to execute the tool.

We use [Graphviz](https://graphviz.org/) to visualize the graphs, so we recommend to install it.

### Start from jar

If someone wants to start the program in an easy way then the jar file (`p4query-jar-with-dependencies.jar`) in the root directory can be used for it. 

Start the visualisation with the 

```sh
$ java -jar p4query-jar-with-dependencies.jar "draw" "-A" analysers
```

command can be used, where the analysers are the analyses, that the user would like to run and visualize. analysers can be: `SyntaxTree`, `AbstractSyntaxTree`, `SymbolTable`, `CallSites` `ControlFlow`, `CallGraph`.

More information can be seen in the [User Guide](docs/user_guide.md).

## Contributing

This research project is mainly developed in an internal repository. 

If you would like to cooperate with us in the project, please contact the authors. If you already know what you're doing, the [contribution guide](docs/CONTRIBUTING.md) can be useful.

## License

P4Query is licensed under the [Apache License, Version 2.0](LICENSE.txt).

## Credits and acknowledgements

Authors:

- [Dániel Lukács](https://github.com/daniel-lukacs/)
- [Gabriella Tóth](https://github.com/tothgabi/)
- [Máté Tejfel](https://github.com/mate-tejfel)

Acknowledgements:

- Thanks for Ali Fattaholmanan for publishing his ANTLR implementation of the P4 grammar.
- Thanks for all the developers for working on the dependencies in the `pom.xml`s. 
