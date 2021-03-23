package p4analyser.broker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Singleton;

import com.beust.jcommander.JCommander;

import org.codejargon.feather.Provides;

import p4analyser.ontology.IllegalUserInputException;
import p4analyser.ontology.providers.AppUI;
import p4analyser.ontology.providers.Application;
import p4analyser.ontology.providers.CLIArgs;

public class CLIArgsProvider {


    private final Application invokedApp;
    private String usage;
    private String commandName;

    CLIArgsProvider(String[] args, Map<String, Application> appRegistry, String defaultP4File) throws IllegalUserInputException, IOException{

        parseArgs(args, appRegistry);

        if (commandName == null) {
            throw new IllegalArgumentException(
                    "Please, provide a command argument." + System.lineSeparator() + usage);
        }

        this.invokedApp = appRegistry.get(commandName);

        AppUI ui = invokedApp.getUI();

        if (ui.help) {
            throw new IllegalUserInputException(usage);
        }

        String actualP4FilePath = ensureP4FileOrDefault(ui.p4FilePath, defaultP4File);

        String actualDbPath = null;
        if (ui.databaseLocation != null) {
            if(ui.reset && ui.readonly){
               throw new IllegalUserInputException(
                        "--reset and -- readonly are conflicting options, use at most one.");
            }

            actualDbPath = 
                ensurePersistingDirectoryExists(ui.databaseLocation, actualP4FilePath);
        }

        ui.init(actualDbPath, actualP4FilePath);
    }

    private void parseArgs(String[] args, Map<String, Application> appRegistry) {
        JCommander.Builder jcb = JCommander.newBuilder();

        for (Application app : appRegistry.values()) {
            AppUI cmd = app.getUI();
            String cmdName = cmd.getCommandName();
            String[] aliases = cmd.getCommandNameAliases();

            jcb.addCommand(cmdName, cmd, aliases);
        }

        JCommander jc = jcb.build();
        jc.parse(args);

        StringBuilder sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);

        this.usage = sb.toString();
        this.commandName = jc.getParsedCommand();
    }

    Application getInvokedApp() {
        return invokedApp;
    }

    AppUI getInvokedAppUI() {
        return invokedApp.getUI();
    }

    @Provides
    @Singleton
    @CLIArgs
    public AppUI provideArgs(){
        return invokedApp.getUI();
    }


    private static String ensureP4FileOrDefault(String inputFile, String defaultInputFile) throws IOException {
        String p4FilePath; 
        if (inputFile == null) {
            System.out.println("warning: no P4 input file argument provided, using basic.p4");
            p4FilePath = defaultInputFile;
            // app.getUI().p4FilePath is left on null. this is consistent with not having user input.
        } else {
            p4FilePath = App.absolutePath(inputFile);
        }

        File p4File = new File(p4FilePath);

        if (!p4File.exists()) {
            throw new IllegalArgumentException("No file exists at " + p4FilePath);
        }
        if (!p4File.isFile()) {
            throw new IllegalArgumentException(p4FilePath + " is not a file.");
        }

        return p4FilePath;
    }


    private static String ensurePersistingDirectoryExists(String databaseLocation, String p4FilePath) {
        File f = new File(databaseLocation);
        if (!f.exists()) {
            throw new IllegalArgumentException("No directory found at " + App.absolutePath(databaseLocation));
        }
        if (!f.isDirectory()) {
            throw new IllegalArgumentException(databaseLocation + " is not a directory");
        }

        String psd = Paths.get(databaseLocation, p4FilePath).toString();
        File psdf = new File(psd);

        if (!psdf.exists() && !psdf.mkdirs()) { // short-circuit
            throw new IllegalStateException("Unable to create directory at " + App.absolutePath(psd));
        }

        return psd;
    }

    
}
