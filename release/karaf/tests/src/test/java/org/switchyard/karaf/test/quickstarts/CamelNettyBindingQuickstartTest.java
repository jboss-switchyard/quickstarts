package org.switchyard.karaf.test.quickstarts;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.common.io.Files;

public class CamelNettyBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.netty.binding";
    private static String featureName = "switchyard-quickstart-camel-netty-binding";

    private static String SRC_DIR = "target/test-classes/quickstarts/camel-netty-binding";
    private static String DEST_DIR_0 = "target/karaf/" + featureName;
    private static String DEST_DIR_2 = "quickstarts/camel-netty-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName, null, null, new PhaseListener() {
            // TODO: Look into using KarafDistributionOption.replaceConfigurationFile(String,File) instead of using a PhaseListener.
            @Override
            public void post(Phase phase) throws Exception {
                if (Phase.START_CONTAINER.equals(phase)) {
                    copy("users.jks", "roles.properties");
                }
            }
            private void copy(String... names) throws Exception {
                for (String name : names) {
                    File srcFile = new File(SRC_DIR, name);
                    File destDir0 = new File(DEST_DIR_0);
                    if (destDir0.exists()) {
                        for (File destDir1 : destDir0.listFiles()) {
                            if (destDir1.isDirectory()) {
                                File destFile = new File(new File(destDir1, DEST_DIR_2), name);
                                Files.copy(srcFile, destFile);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    @Test
    public void testDeployment() throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(new File(SRC_DIR, "users.jks")), "changeit".toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);
        SSLContext context = SSLContext.getInstance("TLS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "changeit".toCharArray());
        context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);
        SSLSocketFactory sf = context.getSocketFactory();
        Socket clientSocket = sf.createSocket("localhost", 3939);
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        outputStream.write(getClass().getName().getBytes());
        outputStream.flush();
        Thread.sleep(1000);
        clientSocket.close();
    }
}
