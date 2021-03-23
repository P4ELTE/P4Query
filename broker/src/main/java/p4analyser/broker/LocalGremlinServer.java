/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4analyser.broker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.inject.Singleton;

import org.apache.commons.lang3.SystemUtils;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.codejargon.feather.Provides;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;



// NOTE For future reference, this guide was helpful: http://emehrkay.com/getting-started-with-tinkerpop-s-gremlin-server-and-gizmo-python
// NOTE The official way would have been to use gremlin-server.sh or gremlin-server.bat.

// NOTE when the blackboard server will be prepared for remote connections, this class will change as well. currently the double layering seems useless. 
public class LocalGremlinServer {

    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static String GREMLIN_SERVER_CONF_PATH;
    private static String TINKERGRAPH_EMPTY_PROPERTIES_PATH;
    private static String EMPTY_SAMPLE_GROOVY_PATH;
    private static Boolean isWindows = SystemUtils.OS_NAME.contains("Windows");

    static {
        try {
            GREMLIN_SERVER_CONF_PATH = contentsToTempFile(
                loader.getResourceAsStream("conf/gremlin-server-min.yaml"), "conf/gremlin-server-min.yaml");
            TINKERGRAPH_EMPTY_PROPERTIES_PATH = contentsToTempFile(loader.getResourceAsStream("conf/tinkergraph-empty.properties"), "conf/tinkergraph-empty.properties");
            EMPTY_SAMPLE_GROOVY_PATH = contentsToTempFile(loader.getResourceAsStream("conf/empty-sample.groovy"), "conf/empty-sample.groovy");
        } catch(IOException e){
            throw new RuntimeException(e);
        }

        updateServerConfig();
    }

    private p4analyser.blackboard.App bb = null;
    private GraphTraversalSource g = null;

    private String defaultStateDirectory = null;
    private boolean reset = false;
    private boolean readonly = false;

    // this is for serialiazation 
    public LocalGremlinServer() {
    }

    public LocalGremlinServer(String defaultStateDirectory, boolean reset, boolean readonly)  {
        this.defaultStateDirectory = defaultStateDirectory;
        this.reset = reset;
        this.readonly = readonly;
    }

    public void init() throws LocalGremlinServerException {
        List<String> args = new LinkedList<>();
        args.add("-c");
        args.add(GREMLIN_SERVER_CONF_PATH);
        if(defaultStateDirectory != null){
            args.add("--store");
            args.add(defaultStateDirectory);
        }
        if(reset){
            args.add("--reset");
        }
        if(readonly){
            args.add("--readonly");
        }

        bb = new p4analyser.blackboard.App(args.toArray(new String[args.size()]));

        try {
            bb.start();
            connect();
        } catch (IOException e) {
            throw new LocalGremlinServerException(e);
        }
    }

    private void connect() throws IOException {

        // cheap way:
        // Graph graph = TinkerGraph.open();
        // GraphTraversalSource g = graph.traversal();
        // return g;

        // TODO read these from gremlin-server.min.yaml, otherwise the info have to
        // maintained at two places
        String host = "localhost";
        int port = 8182;
        String remoteTraversalSourceName = "g";

        disconnect();

        g = AnonymousTraversalSource.traversal()
                .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));
    }

    private void disconnect() throws IOException {
        if (g != null) {
            try {
                g.close();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Provides
    @Singleton
    public GraphTraversalSource provideConnection() throws IOException {
        return g;
    }

    public void close() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        disconnect();

        if(bb!=null)
            bb.close();

    }

    // NOTE: GremlinServer does not seem to substitute "classpath:" inside the YAML, so we have to include the path manually
    private static void updateServerConfig() {
        Path path = Paths.get(GREMLIN_SERVER_CONF_PATH);
        Charset charset = StandardCharsets.UTF_8;

        String content;
        try {
            content = new String(Files.readAllBytes(path), charset);
            
            //In windows we need these: ""
            if (isWindows) {
                content = content.replaceAll("TINKERGRAPH_EMPTY_PROPERTIES", "\"" + TINKERGRAPH_EMPTY_PROPERTIES_PATH.replace("\\", "/") + "\"");
                content = content.replaceAll("EMPTY_SAMPLE_GROOVY", "\"" + EMPTY_SAMPLE_GROOVY_PATH.replace("\\", "/") + "\"");
            } else {
                content = content.replaceAll("TINKERGRAPH_EMPTY_PROPERTIES", TINKERGRAPH_EMPTY_PROPERTIES_PATH);
                content = content.replaceAll("EMPTY_SAMPLE_GROOVY", EMPTY_SAMPLE_GROOVY_PATH);
            }
            
            Files.write(path, content.getBytes(charset));
//            System.out.println(Files.lines(Paths.get(GREMLIN_SERVER_CONF_PATH)).collect(Collectors.toList()));
        } catch (IOException e1) {
            throw new IllegalStateException("Failed to edit config file "+ GREMLIN_SERVER_CONF_PATH);
        }
    }

    private static String contentsToTempFile(InputStream is, String fileName) throws IOException {
        File f = new File(System.getProperty("java.io.tmpdir"), fileName);
        f.getParentFile().mkdirs();
        f.createNewFile();

        Files.copy(is, f.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return f.getAbsolutePath();
    }

// NOTE AnonymousTraversal accepts method parameters so this configuration file is not used currently. Leaving this here for future reference.
//    
//   import org.apache.commons.configuration.Configuration;
//   import org.apache.commons.configuration.ConfigurationException;
//   import org.apache.commons.configuration.PropertiesConfiguration;

//   private static Configuration updateClientConfig() {
//     GREMLIN_CLIENT_CONF_PATH = contentsToTempFile(
//         loader.getResourceAsStream("conf/remote-graph.properties"), "conf/remote-graph.properties");
//       Configuration c;
//       try {
//           c = new PropertiesConfiguration(GREMLIN_CLIENT_CONF_PATH);
//       } catch (ConfigurationException e) {
//           throw new IllegalStateException(
//                   String.format(
//                       "Error parsing Gremlin client file at %s:", 
//                       GREMLIN_CLIENT_CONF_PATH),
//                   e);
//       }
//
//    // NOTE Unlike with YAML, we can use "classpath:" notation in .properties files, so it turned out we don't need this:
//    private static String GREMLIN_CLIENT_CONF_PATH;
//    // c.setProperty("gremlin.remote.driver.clusterFile", GREMLIN_CLIENT_CONF_CLUSTERFILE_PATH);
//
//       return c;
//   }
}
