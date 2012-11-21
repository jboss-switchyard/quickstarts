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

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

@RunWith(Arquillian.class)
public class HornetqBindingQuickstartTest {

    private static final String JMS_PREFIX = "jms.queue.";
    private static final String QUEUE = "GreetingServiceQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp";
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(QUEUE);
        ResourceDeployer.addPropertiesUser(USER, PASSWD);
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-hornetq-binding");
    }

    @Test
    public void testDeployment() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();
        
        try {
            ClientSession session = hqMixIn.getClientSession();
            ClientProducer producer = session.createProducer(JMS_PREFIX+QUEUE);
            ClientMessage message = hqMixIn.createMessage("Tomo");
            producer.send(message);
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(QUEUE);
        }
    }

}
