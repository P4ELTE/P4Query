/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.ontology.providers;

import java.util.List;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public abstract class AppUI {

    private String actualDbLocation;
    private String actualP4FilePath;

    abstract public String getCommandName(); 
    abstract public String[] getCommandNameAliases(); 


    @Parameter(description = "<location of P4 file to be analysed>" /* , required = true  */)
    public String p4FilePath;


    @Parameter(names={"--reset"}, description = "To be used together with the --store option. Run all the analysers again from scratch and overwrite the existing database.")
    public boolean reset;

    @Parameter(help =true, names = { "-?", "--help"}, description = "Lists available options, commands, and command options." )
    public boolean help;

    @Parameter(names={"--readonly", "-r"}, description = "To be used together with the --store option. Data will be loaded from persistent storage, but modifications will not be saved.")
    public boolean readonly;

    @Parameter(names = { "--store", "-s" }, description = "Directory where database is stored. If not specified, in-memory database is launched. If no database exists, one is created.", validateWith = OptionCannotBeValueValidator.class)
    public String databaseLocation;

    @Parameter(names="--misc", description = "(For developers) Arbitrary list of strings that can be read by some of the analysers for development and testing purposes." /* , required = true  */)
    public List<String> misc;

    public void init(String actualDatabaseLocation, String actualP4FilePath){
        this.actualDbLocation = actualDatabaseLocation;
        this.actualP4FilePath = actualP4FilePath;
    }

    public String getActualDbLocation() {
        if(actualDbLocation == null && databaseLocation != null)
            throw new IllegalAccessError(AppUI.class + " should have been initialized, and with non-null values.");
        return actualDbLocation;
    }

    public String getActualP4FilePath() {
        if(actualP4FilePath == null)
            throw new IllegalAccessError(AppUI.class + " should have been initialized, and with non-null values.");
        return actualP4FilePath;
    }

    public static class OptionCannotBeValueValidator implements IParameterValidator {
        public void validate(String name, String value)
            throws ParameterException {
            if (value.startsWith("--") || value.startsWith("-")) {
                throw new ParameterException(value + " is not a valid argument for option " + name); 
            }
        }
    }

    @Override
    public String toString() {
        return "AppUI [databaseLocation=" + databaseLocation + ", help=" + help + ", misc=" + misc + ", p4FilePath="
                + p4FilePath + ", readonly=" + readonly + ", reset=" + reset + "]";
    }



}
