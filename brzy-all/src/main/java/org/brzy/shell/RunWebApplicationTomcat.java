package org.brzy.shell;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Embedded;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;


public class RunWebApplicationTomcat {

    private String path = null;
    private Embedded container = null;

    /**
     * The directory to create the Tomcat server configuration under.
     */
    private String catalinaHome = "target/tomcat";

    /**
     * The port to run the Tomcat server on.
     */
    private int port = 8080;

    /**
     * The classes directory for the web application being run.
     */
    private String classesDir = "target/classes";

    /**
     * The web resources directory for the web application being run.
     */
    private String webappDir = "war";

    /**
     * Creates a single-webapp configuration to be run in Tomcat on port 8089.
     * If module name does not conform to the 'contextname-webapp' convention,
     * use the two-args constructor.
     *
     * @param contextName without leading slash, for example, "mywebapp"
     */
    public RunWebApplicationTomcat(String contextName) {
        path = "/" + contextName;
    }

    /**
     * Starts the embedded Tomcat server.
     *
     * @param port something
     * @throws java.net.MalformedURLException des
     * @throws org.apache.catalina.LifecycleException
     *                                        des
     */
    public void run(int port) throws LifecycleException, MalformedURLException {
        this.port = port;
        // create server
        container = new Embedded();
        container.setCatalinaHome(catalinaHome);
        container.setRealm(new MemoryRealm());

        // create webapp loader
        WebappLoader loader = new WebappLoader(this.getClass().getClassLoader());

        if (classesDir != null)
            loader.addRepository(new File(classesDir).toURI().toURL().toString());

        // create context
        Context rootContext = container.createContext(path, webappDir);
        rootContext.setLoader(loader);
        rootContext.setReloadable(true);

        // create host
        // String appBase = new File(catalinaHome, "webapps").getAbsolutePath();
        Host localHost = container.createHost("localHost", new File("target").getAbsolutePath());
        localHost.addChild(rootContext);

        // create engine
        Engine engine = container.createEngine();
        engine.setName("localEngine");
        engine.addChild(localHost);
        engine.setDefaultHost(localHost.getName());
        container.addEngine(engine);

        // create http connector
        Connector httpConnector = container.createConnector((InetAddress) null, port, false);
        container.addConnector(httpConnector);

        container.setAwait(true);

        // start server
        container.start();

        // add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                stopContainer();
            }
        });
    }

    /**
     * Stops the embedded Tomcat server.
     */
    public void stopContainer() {
        try {
            if (container != null)
                container.stop();
        }
        catch (LifecycleException exception) {
            System.out.println("exception: " + exception);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static void main(String[] args) throws Exception {
        RunWebApplicationTomcat inst = new RunWebApplicationTomcat(args[0]);
        inst.run(8080);
    }

    public int getPort() {
        return port;
    }
}