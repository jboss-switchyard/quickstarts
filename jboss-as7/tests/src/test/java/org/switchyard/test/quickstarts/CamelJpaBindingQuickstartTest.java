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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class CamelJpaBindingQuickstartTest {

    @Deployment(testable = true)
    public static JavaArchive createDeployment() throws IOException {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-camel-jpa-binding");
    }

    private static final String namespace = "urn:switchyard-quickstart:camel-jpa-binding:0.1.0";
    private static final String application = new QName(namespace, "camel-jpa-retrieve-binding").toString();
    private static final String quartzService = new QName(namespace, "PeriodicService").toString();
    private static final String quartzBinding = "_PeriodicService_quartz_1";
    private static final String greetingService = new QName(namespace, "GreetingService").toString();
    private static final String greetingBinding = "_GreetingService_jpa_1";
    private static final String storeReference = new QName(namespace, "StoreReference").toString();
    private static final String storeBinding = "_StoreReference_jpa_1";

    @ArquillianResource
    private ManagementClient _client;

    @Test
    @RunAsClient
    @InSequence(1)
    public void testStopStoreGateway() throws Exception {
        // make at least one event is stored
        Thread.sleep(6000);

        // stop the store gateway
        executeOperation(storeReference, storeBinding, false);
    }

    @Test
    @InSequence(2)
    public void testStoreGatewayStopped(@ArquillianResource InitialContext context) throws Exception {
        Connection connection = null;
        Statement statement = null;
        try {
            // make sure at least one event is generated
            Thread.sleep(6000);

            final DataSource ds = (DataSource) context.lookup("java:jboss/datasources/ExampleDS");
            connection = ds.getConnection();

            // verify there are no events to be processed
            assertEvents(connection, "StoreReference not disabled.", false);

            // send in a message to make sure the greeting service is still
            // running
            statement = connection.createStatement();
            statement.execute("INSERT INTO events (sender,receiver,createdAt) values ('John', 'Rambo', NOW())");
            Assert.assertEquals(1, statement.getUpdateCount());
            statement.close();
            statement = null;

            // verify the event was processed
            Thread.sleep(1000);
            assertEvents(connection, "GreetingService not running.", false);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        }
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void testRestartStoreGateway() throws Exception {
        // stop the processor gateway
        executeOperation(greetingService, greetingBinding, false);

        // restart the store gateway
        executeOperation(storeReference, storeBinding, true);
    }

    @Test
    @InSequence(4)
    public void testGreetingGatewayStopped(@ArquillianResource InitialContext context) throws Exception {
        // make sure at least one event is generated
        Thread.sleep(6000);

        assertEvents(context, "GreetingService not stopped or StoreReference not restarted.", true);
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void testStopQuartzGateway() throws Exception {
        // stop the quartz gateway
        executeOperation(quartzService, quartzBinding, false);

        // start the processor gateway
        executeOperation(greetingService, greetingBinding, true);

        // make sure at least one event is generated
        Thread.sleep(6000);

        // stop the processor gateway
        executeOperation(greetingService, greetingBinding, false);
    }

    @Test
    @InSequence(6)
    public void testQuartzGatewayStopped(@ArquillianResource InitialContext context) throws Exception {
        // make sure at least one event is generated
        Thread.sleep(6000);

        assertEvents(context, "Quartz gateway not stopped.", false);
    }

    private void executeOperation(final String serviceName, final String bindingName, final boolean start)
            throws Exception {
        final ModelNode operation = new ModelNode();
        operation.get(ModelDescriptionConstants.OP_ADDR).add("subsystem", "switchyard");
        operation.get("application-name").set(application);
        if (start) {
            operation.get(ModelDescriptionConstants.OP).set("start-gateway");
        } else {
            operation.get(ModelDescriptionConstants.OP).set("stop-gateway");
        }
        operation.get(ModelDescriptionConstants.NAME).set(bindingName);
        operation.get("service-name").set(serviceName);
        final ModelNode result = _client.getControllerClient().execute(operation);
        Assert.assertEquals("Failed to " + (start ? "start" : "stop") + " gateway: " + result.toString(),
                ModelDescriptionConstants.SUCCESS, result.get(ModelDescriptionConstants.OUTCOME).asString());
    }

    private void assertEvents(final InitialContext context, final String message, final boolean expected)
            throws Exception {
        Connection connection = null;
        try {
            final DataSource ds = (DataSource) context.lookup("java:jboss/datasources/ExampleDS");
            connection = ds.getConnection();
            assertEvents(connection, message, expected);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        }
    }

    private void assertEvents(final Connection connection, final String message, final boolean expected)
            throws Exception {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            boolean containsEvents = statement.execute("SELECT * FROM events");
            if (containsEvents) {
                final ResultSet rs = statement.getResultSet();
                containsEvents = rs.next();
                rs.close();
            }
            Assert.assertEquals(message, expected, containsEvents);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        }
    }
}
