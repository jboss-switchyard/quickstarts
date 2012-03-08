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

import java.io.IOException;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Message;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

/**
 * A testcase for deploy & invoke the bpel-service/jms_binding quickstart on AS7.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(Arquillian.class)
public class BpelServiceJmsBindingQuickstartTest {

    private static final String REQUEST_QUEUE = "HelloRequestQueue";
    private static final String REPLY_QUEUE = "HelloReplyQueue";
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
        
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            session = hqMixIn.createJMSSession();
            producer = session.createProducer(HornetQMixIn.getJMSQueue(REQUEST_QUEUE));
            Message message = hqMixIn.createJMSMessage(INPUT);
            producer.send(message);

            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(REPLY_QUEUE));
            message = consumer.receive(3000);
            String reply = hqMixIn.readStringFromJMSMessage(message);
            SwitchYardTestKit.compareXMLToString(reply, EXPECTED_REPLY);
        } finally {
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

    private static final String EXPECTED_REPLY = "<sayHelloResponse xmlns=\"http://www.jboss.org/bpel/examples\">"
                + "<tns:result xmlns:tns=\"http://www.jboss.org/bpel/examples\">Hello Tomo</tns:result>"
                + "</sayHelloResponse>";

}
