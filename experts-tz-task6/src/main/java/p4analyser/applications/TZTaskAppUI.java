package p4analyser.applications;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import p4analyser.ontology.providers.AppUI;

@Parameters(commandDescription = "Launch my application")
public class TZTaskAppUI extends AppUI {

    @Override
    public String getCommandName() { return "tztask"; }

    @Override
    public String[] getCommandNameAliases() {
        return new String[]{"tztask6", "TZtask", "TZtask6", "task6"};
    }

    @Parameter(names = { "-st", "--syntax-tree" },
               description = "Triggers syntax tree analysis")
    public Boolean synTree;

}
