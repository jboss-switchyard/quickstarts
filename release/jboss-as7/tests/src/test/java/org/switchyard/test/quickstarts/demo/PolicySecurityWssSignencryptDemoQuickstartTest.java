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
package org.switchyard.test.quickstarts.demo;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

/**
 * The policy-security-wss-signencrypt demo quickstart test.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@RunWith(Arquillian.class)
@ServerSetup(PolicySecurityWssSignencryptDemoQuickstartTest.SSLSocketBindingSetupTask.class)
public class PolicySecurityWssSignencryptDemoQuickstartTest {

    private static String SRC_DIR = System.getProperty("jboss.home") + "/quickstarts/demos/policy-security-wss-signencrypt";

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarDemoDeployment("switchyard-demo-policy-security-wss-signencrypt");
    }

    @Test
    public void testUnsecure() throws Exception {
        String response = invokeWorkService("http", 8080, "policy-security-wss-signencrypt", false, null);
        XMLAssert.assertXpathExists("//faultcode", response);
    }

    @Test
    public void testConfidential() throws Exception {
        String response = invokeWorkService("http", 8080, "policy-security-wss-signencrypt", true, null);
        XMLAssert.assertXpathNotExists("//faultcode", response);
    }

    @Test
    public void testConfidentialSecure() throws Exception {
        String response = invokeWorkService("https", 8443, "policy-security-wss-signencrypt", true, setupSSLContext());
        XMLAssert.assertXpathNotExists("//faultcode", response);
    }

    @Test
    public void testSecure() throws Exception {
        String response = invokeWorkService("https", 8443, "policy-security-wss-signencrypt", false, setupSSLContext());
        XMLAssert.assertXpathExists("//faultcode", response);
    }

    private SSLContext setupSSLContext() throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(new File(SRC_DIR, "connector.jks")), "changeit".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "changeit".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }

    private String invokeWorkService(String scheme, int port, String context, boolean signencrypt, SSLContext sslContext) throws Exception {
        String soapRequest;
        if (signencrypt) {
            soapRequest = new StringPuller().pull(new File(SRC_DIR, "src/test/resources/xml/secure-request.xml"));
        } else {
            soapRequest = new StringPuller().pull(new File(SRC_DIR, "src/test/resources/xml/insecure-request.xml"))
                    .replaceAll("WORK_CMD", "CMD-" + System.currentTimeMillis());
        }
        HTTPMixIn http = new HTTPMixIn(sslContext);
        http.initialize();
        try {
            String endpoint = String.format("%s://localhost:%s/%s/WorkService", scheme, port, context);
            return http.postString(endpoint, soapRequest);
        } finally {
            http.uninitialize();
        }
    }

    public static class SSLSocketBindingSetupTask implements ServerSetupTask {

        @Override
        public void setup(ManagementClient client, String unused) throws Exception {
            ResourceDeployer.setupSSL(client, new File(SRC_DIR, "connector.jks").getAbsolutePath(), "changeit");

            Thread.sleep(1000);
        }

        @Override
        public void tearDown(ManagementClient client, String unused) throws Exception {
            ResourceDeployer.tearDownSSL(client);
        }
    }
    
}
