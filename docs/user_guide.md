# P4Query - User Guide

## Usage



All built-in applications of `p4query` can be launched from the command line interface (CLI). For example, assuming you store a P4 program at path `$EXAMPLES/basic.p4`{.sh}, and want launch the graph query REPL, just navigate into the directory where you store `p4query-x-y-z.jar`{.sh} and fire up `p4query` like this in your terminal:

One example is graph visualization, which was implemented as an application:

```sh
$ p4query draw $EXAMPLES/basic.p4 -A SymbolTable ControlFlow
```

Again the first argument tells `p4query` to launch the visualizer application with the following arguments. Specifically, the visualizer will load the file into knowledge graph (unless it is loaded already), and then show you a subgraph that contains only the symbol table and control flow edges. For more information on graph visualization, see [Visualizer](TODO).


Finally, to formally verify that `$EXAMPLES/basic.p4`{.sh} is bug-free (at least with respect some bugs), simply run:

```sh
$ p4query verify $EXAMPLES/basic.p4 
```

For more information on P4 formal verification, see [Verification](TODO).


### Persistent database

Syntactic and semantic analysis can take a long time, so it is faster if they do not have to be performed every time an application is started. 

To persist the database, use the `--store <location>` option with the location where the database (or databases if you invoke the tool on multiple files) will be stored. If you run the same application twice on the same location, the second execution will take much less time since the database is already complete. Moreover, if you run a different application, only those analyses will be performed whose results are not yet in the database.




