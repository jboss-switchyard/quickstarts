/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.test.quickstarts.demo;

import java.io.IOException;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

/**
 * The policy-transaction demo quickstart test.
 */
@RunWith(Arquillian.class)
public class PolicyTransactionDemoQuickstartTest {

    private static final String QUEUE = "policyQSTransacted";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp";
    private static final String COMMAND = "rollback.B";
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(QUEUE);
        ResourceDeployer.addPropertiesUser(USER, PASSWD);
        return ArquillianUtil.createJarDemoDeployment("switchyard-quickstart-demo-policy-transaction");
    }

    @Test
    public void test() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE));
            Message message = hqMixIn.createJMSMessage(COMMAND);
            producer.send(message);
            Thread.sleep(1000);
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(QUEUE);
        }
    }
}
