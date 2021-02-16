/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.applications.tests;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import p4analyser.ontology.providers.AppUI;

@Parameters(commandDescription = "Launch the graph drawing application")
public class TCommand extends AppUI {


    @Override
    public String getCommandName() {
        return "test";
    }

    @Parameter(names = { "-A", "--analysers"}, description = "<descriptors of analysers who will be needed for the tests>", validateWith = OptionCannotBeValueValidator.class, variableArity = true)
    public List<String> names;

    @Override
    public String[] getCommandNameAliases() {
        return new String[]{};
    }

    @Override
    public String toString() {
        return "TCommand [super= " + super.toString() + "]";
    }
}