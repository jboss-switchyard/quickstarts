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
package org.switchyard.test.quickstarts.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

/**
 * ResouceDeployer is capable to deploy different types of resources
 * to a running JBoss AS7 server.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 * @author Daniel Bevenius
 *
 */
public class ResourceDeployer {
    
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 9999;
    public static final String USER = "guest";
    public static final String PASSWD = "Guestp123!";
    
    private ResourceDeployer() {
    }
    
    public static ModelNode addQueue(final String host, final int port, final String queueName, final String jndiName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        final ModelNode op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("subsystem", "messaging");
        op.get("address").add("hornetq-server", "default");
        op.get("address").add("jms-queue", queueName);
        op.get("entries").add(jndiName)
                         .add("java:jboss/exported/jms/" + jndiName);
        op.get("durable").set(false);

        return client.execute(op);
    }

    public static ModelNode addQueue(final String host, final int port, final String queueName) throws IOException {
        return addQueue(host, port, queueName, queueName);
    }

    public static ModelNode addQueue(final String queueName) throws IOException {
        return addQueue(DEFAULT_HOST, DEFAULT_PORT, queueName, queueName);
    }

    public static ModelNode addQueue(final String queueName, final String jndiName) throws IOException {
        return addQueue(DEFAULT_HOST, DEFAULT_PORT, queueName, jndiName);
    }

    public static ModelNode removeQueue(final String host, final int port, final String queueName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        final ModelNode op = new ModelNode();
        op.get("operation").set("remove");
        op.get("address").add("subsystem", "messaging");
        op.get("address").add("hornetq-server", "default");
        op.get("address").add("jms-queue", queueName);
        return client.execute(op);
    }

    public static ModelNode removeQueue(final String queueName) throws IOException {
        return addQueue(DEFAULT_HOST, DEFAULT_PORT, queueName);
    }

    public static ModelNode addPooledConnectionFactory(final String host, final int port, final String cfName, final boolean xa, final String jndiName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        final ModelNode op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("subsystem", "messaging");
        op.get("address").add("hornetq-server", "default");
        op.get("address").add("pooled-connection-factory", cfName);
        op.get("transaction").set(xa ? "xa" : "local");
        op.get("connector").set("in-vm", "");
        op.get("entries").add(jndiName);
        return client.execute(op);
    }

    public static ModelNode addPooledConnectionFactory(final String cfName, final boolean xa, final String jndiName) throws IOException {
        return addPooledConnectionFactory(DEFAULT_HOST, DEFAULT_PORT, cfName, xa, jndiName);
    }

    public static ModelNode removePooledConnectionFactory(final String host, final int port, final String cfName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        final ModelNode op = new ModelNode();
        op.get("operation").set("remove");
        op.get("address").add("subsystem", "messaging");
        op.get("address").add("hornetq-server", "default");
        op.get("address").add("pooled-connection-factory", cfName);
        return client.execute(op);
    }

    public static ModelNode removePooledConnectionFactory(final String cfName) throws IOException {
        return removePooledConnectionFactory(DEFAULT_HOST, DEFAULT_PORT, cfName);
    }

    private static ModelControllerClient createClient(final String host, final int port) throws UnknownHostException {
        return ModelControllerClient.Factory.create(InetAddress.getByName(host), port);
    }

}
