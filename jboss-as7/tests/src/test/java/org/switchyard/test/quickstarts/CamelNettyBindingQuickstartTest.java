/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.test.quickstarts;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
@ServerSetup(CamelNettyBindingQuickstartTest.SecuritySetupTask.class)
public class CamelNettyBindingQuickstartTest {

    private static String SRC_DIR = System.getProperty("jboss.home") + "/quickstarts/camel-netty-binding";

    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-netty-binding");
    }

    @Test
    public void testTCPSecure() throws Exception {
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

    @Test
    public void testTCPUnsecure() throws Exception {
        Socket clientSocket = new Socket("localhost", 3939);
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        outputStream.write(getClass().getName().getBytes());
        Thread.sleep(1000);
        clientSocket.close();
    }

    @Test
    public void testUDPUnsecure() throws Exception {
        MulticastSocket clientSocket = new MulticastSocket();
        InetAddress group = InetAddress.getByName("localhost");

        byte[] datagramBody = getClass().getName().getBytes();
        DatagramPacket packet = new DatagramPacket(datagramBody, 0, datagramBody.length, group, 3940);
        clientSocket.send(packet);

        Thread.sleep(1000);
        clientSocket.close();
    }

    public static class SecuritySetupTask implements ServerSetupTask {

        @Override
        public void setup(ManagementClient client, String unused) throws Exception {
            ModelNode op = new ModelNode();
            op.get("operation").set("add");
            op.get("address").add("subsystem", "security");
            op.get("address").add("security-domain", "netty-security-cert");
            op.get("cache-type").set("default");
            client.getControllerClient().execute(op);

            op.remove("cache-type");
            op.get("address").add("authentication", "classic");
            ModelNode loginModule = new ModelNode();
            loginModule.get("code").set("org.switchyard.security.login.CertificateLoginModule");
            loginModule.get("flag").set("required");
            loginModule.get("module-options").add("keyStoreLocation",
                    new File(SRC_DIR, "users.jks").getAbsolutePath());
            loginModule.get("module-options").add("keyStorePassword", "changeit");
            loginModule.get("module-options").add("rolesProperties",
                    new File(SRC_DIR, "roles.properties").getAbsolutePath());
            op.get("login-modules").add(loginModule);
            op.get("operation-headers", "allow-resource-service-restart").set("true");
            client.getControllerClient().execute(op);
            
            Thread.sleep(1000);
        }

        @Override
        public void tearDown(ManagementClient client, String unused) throws Exception {
            ModelNode op = new ModelNode();
            op.get("operation").set("remove");
            op.get("address").add("subsystem", "security");
            op.get("address").add("security-domain", "netty-security-cert");
            client.getControllerClient().execute(op);
        }
    }
}
