/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.test.quickstarts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.mixins.HTTPMixIn;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;
import org.w3c.dom.NodeList;

/**
 * A testcase for deploy & invoke the bpel-service/jms_binding quickstart on AS7.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(Arquillian.class)
public class BpelServiceJmsBindingQuickstartTest {

    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://127.0.0.1:4447";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String REQUEST_QUEUE = "HelloRequestQueue";
    private static final String REQUEST_QUEUE_JNDI = "jms/" + REQUEST_QUEUE;
    private static final String REPLY_QUEUE = "HelloReplyQueue";
    private static final String REPLY_QUEUE_JNDI = "jms/" + REPLY_QUEUE;
    private static final String USER = "guest";
    private static final String PASSWD = "guestp";
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(REQUEST_QUEUE);
        ResourceDeployer.addQueue(REPLY_QUEUE);
        ResourceDeployer.addPropertiesUser(USER, PASSWD);
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-bpel-service-jms-binding");
    }

    @Test
    public void testJmsBinding() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();
        
        InitialContext initialContext = null;
        
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            props.put(Context.PROVIDER_URL, PROVIDER_URL);
            props.put(Context.SECURITY_PRINCIPAL, USER);
            props.put(Context.SECURITY_CREDENTIALS, PASSWD);
            initialContext = new InitialContext(props);
            ConnectionFactory factory = (ConnectionFactory)initialContext.lookup(CONNECTION_FACTORY);
            connection = factory.createConnection(USER, PASSWD);
            connection.start();
            
            Destination requestQueue = (Destination)initialContext.lookup(REQUEST_QUEUE_JNDI);
            Destination replyQueue = (Destination)initialContext.lookup(REPLY_QUEUE_JNDI);
            
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(requestQueue);
            producer.send(session.createTextMessage(INPUT));
            session.close();
            
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(replyQueue);
            TextMessage message = (TextMessage)consumer.receive(3000);
            
            Assert.assertNotNull(message);
            Assert.assertEquals(EXPECTED_REPLY, message.getText());
        } finally {
            if (producer != null) {
                    producer.close();
            }
            if (consumer != null) {
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
            hqMixIn.uninitialize();
        }
    }

    @After
    public void undeploy() throws IOException {
        ResourceDeployer.removeQueue(REQUEST_QUEUE);
        ResourceDeployer.removeQueue(REPLY_QUEUE);
    }
    
    private static final String INPUT = "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
                + "<exam:input>Tomo</exam:input>"
                + "</exam:sayHello>";

    private static final String EXPECTED_REPLY = "<sayHelloResponse xmlns=\"http://www.jboss.org/bpel/examples\">\n"
                + "  <tns:result xmlns:tns=\"http://www.jboss.org/bpel/examples\">Hello Tomo</tns:result>\n"
                + "</sayHelloResponse>";
    
    private static final String SOAP_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://www.jboss.org/bpel/examples\">\n" +
		"   <soapenv:Header/>\n" +
		"   <soapenv:Body>\n" +
		"       <exam:sayHello>\n" +
		"           <exam:input>Fred</exam:input>\n" +
		"       </exam:sayHello>\n" +
		"   </soapenv:Body>\n" +
		"</soapenv:Envelope>";
}
