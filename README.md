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

For trying the tool, download the [release](https://github.com/P4ELTE/P4Query/releases/download/v1.0/p4query-v1.0-prealpha.zip), unpack the ZIP and launch it using the following command: 

```sh
$ ./p4query.cmd draw -A ControlFlow
```

This will create an SVG of the control flow graph in your temp directory for the built-in [basic.p4](broker/src/main/resources/basic.p4) file. For more information on parameters (including supplying your own P4 file), please see [User Guide](docs/user_guide.md).

Note: if the command doesn't work for you, you can also try launching the JAR in the ZIP file directly (`$ java -jar pquery.jar  draw -A ControlFlow`).

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

- Thanks for Ali Fattaholmanan for publishing his [ANTLR implementation of the P4 grammar](https://github.com/fattaholmanan/p4-vscode-extension/blob/master/grammar/P4.g4).
- Thanks for all the developers for working on the dependencies in the `pom.xml`s. 
