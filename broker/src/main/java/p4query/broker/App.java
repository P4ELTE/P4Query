/**
 * Copyright 2020-2021, Eötvös Loránd University.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package p4query.broker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.thoughtworks.xstream.XStream;

import org.codejargon.feather.Feather;
import org.codejargon.feather.Key;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import p4query.ontology.IllegalUserInputException;
import p4query.ontology.providers.AppUI;
import p4query.ontology.providers.Application;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class App {

    // used for default includes and input file
    public static final String CORE_P4_PATH = "core.p4";
    public static final String V1MODEL_P4_PATH = "v1model.p4";
    public static final String BASIC_P4_PATH = "basic.p4";

    // used for implementation discovery
    private static final String EXPERTS_PACKAGE = "p4query.experts";
    private static final String APPLICATIONS_PACKAGE = "p4query.applications";
    private static final String ANALYSES_PACKAGE = "p4query.ontology.analyses";

    // TODO a white list would be better
    // NOTE: before you extend this list with your class, check out the --readonly
    // option
    private static final Collection<Class<?>> DO_NOT_SERIALIZE = Arrays.asList(GraphTraversalSource.class);

    private final Map<Class<? extends Annotation>, Object> analysers;
    private final Map<String, Application> apps;
    private final CLIArgsProvider cli;
    private final Feather feather;
    private final LocalGremlinServer server;

    // NOTE: field order matters
    // NOTE: loading files from classpath seems to be platform-dependent, so we load them as streams and write them to temp files
    private final ClassLoader loader =  Thread.currentThread().getContextClassLoader();
    private final String actualBasicP4 = contentsToTempFile(loader.getResourceAsStream(BASIC_P4_PATH), "basic.p4");
    private P4FileService pfs;

    public static void main(String[] args) throws Exception {

        // TODO use finally (and after that, check that database persistence still works consistently between runs and crashes)
	long startTimeApp = System.currentTimeMillis();
        try {
            App broker = new App(args);
            broker.run();
            broker.close();
        } catch (IllegalUserInputException e) {
            System.out.println(e.getMessage());
        } // otherwise: crash


        long stopTimeApp = System.currentTimeMillis();
        System.out.println(String.format("Total time used: %s ms.", stopTimeApp - startTimeApp));
        System.exit(0);
    }


    public App(String[] args) throws DiscoveryException, IOException, LocalGremlinServerException,
            ClassNotFoundException, ReflectionException, IllegalUserInputException {

        File includeDir = new File(System.getProperty("java.io.tmpdir") + "/" + "p4query" + "/" + "include");
        includeDir.getParentFile().mkdirs();
        includeDir.mkdir();

        analysers = App.discoverAnalysers();
        System.out.println("Analysers discovered: " + analysers);

        apps = App.discoverApplications();
        System.out.println("Applications discovered:" + apps);

        cli = new CLIArgsProvider(args, apps, actualBasicP4);
        System.out.println("Command:" + cli.getInvokedAppUI().getCommandName() + " " + cli.getInvokedAppUI());

        storeClasspathIncludes(includeDir);

        List<String> includes = new LinkedList<>();
        includes.add(includeDir.getAbsolutePath());
        includes.addAll(cli.getInvokedAppUI().includes);

        String p4FilePath = cli.getInvokedAppUI().getActualP4FilePath();
        pfs = new P4FileService(p4FilePath, includes);

        server = initGremlinServer(cli);

        feather = initDI(cli, pfs, analysers, server);
    }

    public void run() throws Exception {

        AppUI ui = cli.getInvokedAppUI();

        // run the experts (except the ones the application lazily initializes)
        feather.injectFields(cli.getInvokedApp());

        // run the application
        cli.getInvokedApp().run();

        if (ui.getActualDbLocation() != null) {
            if (!ui.readonly) {
                App.saveInjector(ui.getActualDbLocation(), feather);
            } else
                System.out.println("--readonly argument found, modifications are not saved");
        }

    }

    public void close() throws Exception {
      server.close();
    }

    public GraphTraversalSource getGraphTraversalSource() throws Exception {
      return server.provideConnection();
    }

    public static void storeClasspathIncludes(File includeDir) throws IOException {
        String path = "include";
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        List<String> contents = null;
        try (
                final InputStream is = loader.getResourceAsStream(path);
                final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                final BufferedReader br = new BufferedReader(isr)) {
            contents = br.lines().collect(Collectors.toList());
        }

        for (String fname : contents) {
            try {
                File f = new File(includeDir.getAbsolutePath() + "/" + fname);
                f.createNewFile();
                Files.copy(loader.getResourceAsStream(path + "/" + fname), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch(IOException e){
                throw new IllegalStateException("Cannot open P4 files in built-in include directory", e);
            }
        }
    }

    private static String contentsToTempFile(InputStream is, String fileName) throws IOException {
        File f = new File(System.getProperty("java.io.tmpdir"), fileName);
        f.getParentFile().mkdirs();
        f.createNewFile();

        Files.copy(is, f.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return f.getAbsolutePath();
    }


    public static Map<String, Application> discoverApplications() throws DiscoveryException {
        Reflections reflections = new Reflections(APPLICATIONS_PACKAGE);

        Set<Class<? extends Application>> appImpls = reflections.getSubTypesOf(Application.class);
        if (appImpls.isEmpty()) {
            String msg = String.format("No applications found in " + APPLICATIONS_PACKAGE);
            throw new IllegalStateException(msg);
        }

        Map<String, Application> apps = new HashMap<>();
        for (Class<? extends Application> m : appImpls) {
            Object candid;
            try {
                candid = m.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException
                    | IllegalArgumentException | NoSuchMethodException e) {
                throw new DiscoveryException(e);
            }
            if (!(candid instanceof Application)) {
                throw new IllegalStateException(String.format("Application %s does not implement interface %s",
                        Application.class.getSimpleName()));
            }

            Application app = (Application) candid;

            if (apps.get(app.getUI().getCommandName()) != null)
                throw new IllegalStateException("Ambiguous application name " + app.getUI().getCommandName());

            apps.put(app.getUI().getCommandName(), app);
        }

        return apps;
    }

    public static Map<Class<? extends Annotation>, Object> discoverAnalysers() throws DiscoveryException {

        Reflections reflections = new Reflections(EXPERTS_PACKAGE, new MethodAnnotationsScanner());

        Map<Class<? extends Annotation>, Object> analyserImplemms = new HashMap<>();
        for (Class<? extends Annotation> analysis : discoverAnalyses()) {
            Set<Method> methods = reflections.getMethodsAnnotatedWith(analysis);
            if (methods.isEmpty()) {
                String msg = String.format("No implementation found in %s for analysis %s", EXPERTS_PACKAGE,
                        analysis.getSimpleName());
                throw new IllegalStateException(msg);
            }

            Collection<Class<?>> implems = methods.stream().map(m -> m.getDeclaringClass())
                    .collect(Collectors.toList());
            if (implems.size() > 1) {
                String msg = String.format("Ambigous implementations found in %s for analysis %s: %s", EXPERTS_PACKAGE,
                        analysis.getSimpleName(), implems.stream().map(c -> c.getSimpleName()).toArray());
                throw new IllegalStateException(msg);
            }

            try {
                analyserImplemms.put(analysis, implems.iterator().next().getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException
                    | IllegalArgumentException | NoSuchMethodException e) {
                throw new DiscoveryException(e);
            }

        }
        return analyserImplemms;
    }

    public static Collection<Class<? extends Annotation>> discoverAnalyses() {
        Reflections reflections = new Reflections(ANALYSES_PACKAGE);

        Set<Class<? extends Annotation>> analyses = reflections.getSubTypesOf(Annotation.class);

        return analyses;

    }

    private static LocalGremlinServer initGremlinServer(CLIArgsProvider cliArgs) throws LocalGremlinServerException {
        LocalGremlinServer server;
        AppUI ui = cliArgs.getInvokedAppUI();
        if (ui.getActualDbLocation() == null) {
            // Start the server in temporary mode.
            server = new LocalGremlinServer();
        } else {
            server = new LocalGremlinServer(ui.getActualDbLocation(), ui.reset, ui.readonly);
        }

        server.init();
        return server;
    }

    private static Feather initDI(CLIArgsProvider cli,P4FileService pfs, Map<Class<? extends Annotation>, Object> analysers,
            LocalGremlinServer server)
            throws LocalGremlinServerException, ClassNotFoundException, IOException, ReflectionException {

        AppUI ui = cli.getInvokedAppUI();


        Feather feather = createInjector(cli, pfs, analysers, server);

        try {
            // Updates the injector object with the status of the completed dependencies.
            if (!ui.reset) {
                App.loadInjector(ui.getActualDbLocation(), feather);
            } else {
                System.out.println("--reset argument found, not going to load existing database");
            }
        } catch (FileNotFoundException e) {
            // There is no file at the location yet.
            // This means nothing was done before, no need to update the injector.
        }
        return feather;
    }

    public static String absolutePath(String relativePath) {
        File b = new File(relativePath);

        try {
            return b.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Feather createInjector(CLIArgsProvider cli, P4FileService pfs, Map<Class<? extends Annotation>, Object> analysers,
            LocalGremlinServer server) {


        Collection<Object> deps = new ArrayList<>();
        deps.add(pfs);
        deps.add(cli);
        deps.add(server);

        for (Object analyser : analysers.values()) {
            deps.add(analyser);
        }

        Feather feather = Feather.with(deps.toArray());

        return feather;
    }

    public static Map<String, Object> appCommands(Map<String, Application> apps)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Map<String, Object> cmds = new HashMap<>();
        for (Map.Entry<String, Application> entry : apps.entrySet()) {
            AppUI command = entry.getValue().getUI();
            cmds.put(entry.getKey(), command);
        }
        return cmds;
    }

    public static void loadInjector(String persistentStatePath, Feather feather)
            throws IOException, ReflectionException, ClassNotFoundException {

        XStream xstream = new XStream();
        ObjectInputStream in = xstream
                .createObjectInputStream(new FileInputStream(Paths.get(persistentStatePath, "state.xml").toString()));
        Map<Key, Object> singletons = (Map) in.readObject();
        in.close();

        try {
            Field f = Feather.class.getDeclaredField("singletons");
            f.setAccessible(true);

            f.set(feather, singletons);

            System.out.println("Deserialized the injector state: " + singletons);

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    // NOTE: this is very hacky.
    // for serialization, we tear the inner state of the injector from a private
    // field and serialize that.
    // for deserialization, we squeeze the inner state back to the private field of
    // the injector.
    public static void saveInjector(String persistentStatePath, Feather feather)
            throws IOException, ReflectionException {

        Map<Key, Object> singletons;
        try {
            Field f = Feather.class.getDeclaredField("singletons");
            f.setAccessible(true);
            singletons = (Map) f.get(feather);
            for (Class<?> clazz : DO_NOT_SERIALIZE) {
                singletons.remove(Key.of(clazz));
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }

        Path statePath = Paths.get(persistentStatePath, "state.xml");
        Files.deleteIfExists(statePath);
        XStream xstream = new XStream();
        ObjectOutputStream out = xstream.createObjectOutputStream(new FileOutputStream(statePath.toString()));
        out.writeObject(singletons);
        out.close();
        System.out.println("Serialized the injector state: " + singletons);
    }

}
