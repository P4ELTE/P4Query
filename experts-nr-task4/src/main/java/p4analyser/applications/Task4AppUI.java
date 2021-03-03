package p4analyser.applications;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import p4analyser.ontology.providers.AppUI;

@Parameters(commandDescription = "Launch task4")
public class Task4AppUI extends AppUI {

    @Override
    public String getCommandName() { return "task4"; }

    @Override
    public String[] getCommandNameAliases() {
        return new String[]{};
    }

    @Parameter(names = { "-st", "--syntax-tree" },
               description = "Triggers syntax tree analysis") Boolean synTree;

}
