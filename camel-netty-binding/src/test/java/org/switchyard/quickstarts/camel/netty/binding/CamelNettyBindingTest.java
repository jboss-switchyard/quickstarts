/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.quickstarts.camel.netty.binding;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = {CDIMixIn.class}
)
@RunWith(SwitchYardRunner.class)
public class CamelNettyBindingTest {

    private SwitchYardTestKit _testKit;
    private final static String PAYLOAD = "Keith";

    @BeforeClass
    public static void beforeTest() {
        System.setProperty("java.security.auth.login.config", "src/test/resources/jaas.conf");
    }

    @Test
    public void sendTextMessageThroughTcp() throws Exception {
        // replace existing implementation for testing purposes
        _testKit.removeService("SecuredGreetingService");
        final MockHandler greetingService = _testKit.registerInOnlyService("SecuredGreetingService");
        greetingService.forwardInToOut();

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("users.jks"), "changeit".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);

        SSLContext context = SSLContext.getInstance("TLS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "changeit".toCharArray());

        context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory sf = context.getSocketFactory();

        Socket clientSocket = sf.createSocket("localhost", 3939);
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        // lets write payload directly as bytes to avoid encoding mismatches
        outputStream.write(PAYLOAD.getBytes());
        outputStream.flush();

        // sleep a bit to receive message on camel side
        Thread.sleep(50);
        clientSocket.close();

        greetingService.waitForOKMessage();
        final LinkedBlockingQueue<Exchange> recievedMessages = greetingService.getMessages();
        assertThat(recievedMessages, is(notNullValue()));
        final Exchange recievedExchange = recievedMessages.iterator().next();
        assertThat(PAYLOAD, is(equalTo(recievedExchange.getMessage().getContent(String.class))));
    }

    @Test
    public void sendTextMessageThroughUdp() throws Exception {
        // replace existing implementation for testing purposes
        _testKit.removeService("DefaultGreetingService");
        final MockHandler greetingService = _testKit.registerInOnlyService("DefaultGreetingService");

        MulticastSocket clientSocket = new MulticastSocket();
        InetAddress group = InetAddress.getByName("localhost");
        byte[] datagramBody = PAYLOAD.getBytes(Charset.defaultCharset());
        DatagramPacket packet = new DatagramPacket(datagramBody, 0, datagramBody.length, group, 3940);
        clientSocket.send(packet);

        // sleep a bit to receive message on camel side
        clientSocket.close();

        greetingService.waitForOKMessage();
        final LinkedBlockingQueue<Exchange> recievedMessages = greetingService.getMessages();
        assertThat(recievedMessages, is(notNullValue()));
        final Exchange recievedExchange = recievedMessages.iterator().next();
        String content = recievedExchange.getMessage().getContent(String.class);
        // the receive content is trimmed because extra bytes appended to frame by receiver
        assertThat(PAYLOAD, is(equalTo(content.trim())));
    }

}
