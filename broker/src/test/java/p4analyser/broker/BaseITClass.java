package p4analyser.broker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.codejargon.feather.Feather;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import p4analyser.ontology.providers.Application;
import p4analyser.ontology.providers.AppUI;
import p4analyser.applications.tests.TApp;



public class BaseITClass {

    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static String GREMLIN_CLIENT_CONF_PATH;

    private static String CORE_P4 ;
    private static String V1MODEL_P4;
    private static String p4File ;

    private Feather feather;
    private LocalGremlinServer server;
    public  GraphTraversalSource g;
    private Map<Class<? extends Annotation>, Object> analysers;
    private Map<String, Application> apps;
    private List<String> analysesNames;
    private TApp invokedApp;

    private static Boolean open; 
    private String p4FilePath;

    public void preTests(String file, List<String> inApps) {
        p4File = file;
        analysesNames = inApps; 

        try {
            GREMLIN_CLIENT_CONF_PATH = contentsToTempFile(
                loader.getResourceAsStream("conf/remote-graph.properties"), "conf/remote-graph.properties");
            CORE_P4 = contentsToTempFile(loader.getResourceAsStream("core.p4"), "core.p4");
            V1MODEL_P4 = contentsToTempFile(loader.getResourceAsStream("v1model.p4"), "v1model.p4");
            p4FilePath = contentsToTempFile(loader.getResourceAsStream(p4File), p4File);
        } catch(IOException e){
            System.out.println("IN");
            throw new RuntimeException(e);
        }

        loadClientConfig();
        
        try {
            analysers = App.discoverAnalysers();
            System.out.println("Analysers discovered: " + analysers);

            apps = App.discoverApplications();
            System.out.println("Applications discovered:" + apps);

            invokedApp = (TApp) apps.get("test");
            invokedApp.getUI().misc = analysesNames;

            p4FilePath = ensureP4File(p4FilePath);

            initBrokerState();

            g = invokedApp.getGraphTraversalSource();

             
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void postTest() throws Exception {
        if (open)
            server.close();
    }

    private static String contentsToTempFile(InputStream is, String fileName) throws IOException {
        File f = new File(System.getProperty("java.io.tmpdir"), fileName);
        f.getParentFile().mkdirs();
        f.createNewFile();

        Files.copy(is, f.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return f.getAbsolutePath();
    }


    private static String ensureP4File(String inputFile) throws IOException {
        File p4File = new File(inputFile);

        if (!p4File.exists()) {
            throw new IllegalArgumentException("No file exists at " + inputFile);
        }
        if (!p4File.isFile()) {
            throw new IllegalArgumentException(inputFile + " is not a file.");
        }

        return inputFile;
    }

    private void initBrokerState() throws LocalGremlinServerException, ClassNotFoundException, IOException,
            ReflectionException, Exception {
        server = new LocalGremlinServer();

        server.init();
        open = true;

        feather = createInjector(p4FilePath, invokedApp.getUI(), analysers, server);
        
        feather.injectFields(invokedApp);

        invokedApp.run();
    }


    static String absolutePath(String relativePath)  {
        File b = new File(relativePath);
        
        try {
            return b.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    private static Feather createInjector(String p4FilePath, AppUI cmdArgs, Map<Class<? extends Annotation>, Object> analysers,
    LocalGremlinServer server)  {
        P4FileService p4FileService = new P4FileService(p4FilePath, CORE_P4, V1MODEL_P4);      

        CLIArgsProvider cli = new CLIArgsProvider(cmdArgs);
        Collection<Object> deps = new ArrayList<>();
        
        deps.add(p4FileService);
        deps.add(cli);
        deps.add(server);

        for (Object analyser : analysers.values()) {
            deps.add(analyser);
        }

        Feather feather = Feather.with(deps.toArray());

        return feather;
    }

    private static Configuration loadClientConfig() {
        Configuration c;
        try {
            c = new PropertiesConfiguration(GREMLIN_CLIENT_CONF_PATH);
        } catch (ConfigurationException e) {
            throw new IllegalStateException(
                    String.format(
                        "Error parsing Gremlin client file at %s:", 
                        GREMLIN_CLIENT_CONF_PATH),
                    e);
        }

        return c;
    }
}