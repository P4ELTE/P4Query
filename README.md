Copyright 2020, Eötvös Loránd University.
All rights reserved.

# P4Query

## Description

`P4Query` is a static code analysis framework for the P4 language. We intend `P4Query` to be a knowledge base or infrastructure that can host various applications, such as compilers, code comprehension tools, IDEs, and formal verification tools.

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

## Getting started

### Start from jar

If someone wants to start the program in an easy way then the jar file (`p4query-jar-with-dependencies.jar`) in the root directory can be used for it. 

Start the visualisation with the 

```sh
$ java -jar p4query-jar-with-dependencies.jar "draw" "-A" analysers
```

command can be used, where the analysers are the analyses, that the user would like to run and visualize. analysers can be: `SyntaxTree`, `AbstractSyntaxTree`, `SymbolTable`, `CallSites` `ControlFlow`, `CallGraph`.

More information can be seen in the [User Guide](docs/user_guide.md).

### Dependencies

We use [Graphviz](https://graphviz.org/) to visualize the graphs, so we recommend to install it.

## Contributing

If you would like to cooperate with us in the project, the [description of the contributing](docs/CONTRIBUTING.md) can be useful.

## Credits

## License

[TODO](NOTICE.md)



