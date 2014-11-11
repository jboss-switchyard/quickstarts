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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.as.arquillian.container.ManagementClient;
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
        try {
            final ModelNode op = new ModelNode();
            op.get("operation").set("add");
            op.get("address").add("subsystem", "messaging");
            op.get("address").add("hornetq-server", "default");
            op.get("address").add("jms-queue", queueName);
            op.get("entries").add(jndiName)
                             .add("java:jboss/exported/jms/" + jndiName);
            op.get("durable").set(false);
    
            return client.execute(op);
        } finally {
            client.close();
        }
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
        try {
            final ModelNode op = new ModelNode();
            op.get("operation").set("remove");
            op.get("address").add("subsystem", "messaging");
            op.get("address").add("hornetq-server", "default");
            op.get("address").add("jms-queue", queueName);
            return client.execute(op);
        } finally {
            client.close();
        }
    }

    public static ModelNode removeQueue(final String queueName) throws IOException {
        return addQueue(DEFAULT_HOST, DEFAULT_PORT, queueName);
    }

    public static ModelNode addPooledConnectionFactory(final String host, final int port, final String cfName, final boolean xa, final String jndiName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        try {
            final ModelNode op = new ModelNode();
            op.get("operation").set("add");
            op.get("address").add("subsystem", "messaging");
            op.get("address").add("hornetq-server", "default");
            op.get("address").add("pooled-connection-factory", cfName);
            op.get("transaction").set(xa ? "xa" : "local");
            op.get("connector").set("in-vm", "");
            op.get("entries").add(jndiName);
            return client.execute(op);
        } finally {
            client.close();
        }
    }

    public static ModelNode addPooledConnectionFactory(final String cfName, final boolean xa, final String jndiName) throws IOException {
        return addPooledConnectionFactory(DEFAULT_HOST, DEFAULT_PORT, cfName, xa, jndiName);
    }

    public static ModelNode removePooledConnectionFactory(final String host, final int port, final String cfName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        try {
            final ModelNode op = new ModelNode();
            op.get("operation").set("remove");
            op.get("address").add("subsystem", "messaging");
            op.get("address").add("hornetq-server", "default");
            op.get("address").add("pooled-connection-factory", cfName);
            return client.execute(op);
        } finally {
            client.close();
        }
    }

    public static ModelNode removePooledConnectionFactory(final String cfName) throws IOException {
        return removePooledConnectionFactory(DEFAULT_HOST, DEFAULT_PORT, cfName);
    }

    public static void setupSSL(ManagementClient client, String keystorePath, String keystorePassword) throws Exception {
        // EAP
        ModelNode op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("subsystem", "web");
        op.get("address").add("connector", "https");
        op.get("socket-binding").set("https");
        op.get("scheme").set("https");
        op.get("protocol").set("HTTP/1.1");
        op.get("secure").set("true");
        op.get("enabled").set("true");
        op.get("enable-lookups").set("false");
        op.get("operation-headers", "allow-resource-service-restart").set("true");
        client.getControllerClient().execute(op);

        op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("subsystem", "web");
        op.get("address").add("connector", "https");
        op.get("address").add("ssl", "configuration");
        op.get("name").set("https");
        op.get("password").set(keystorePassword);
        op.get("certificate-key-file").set(keystorePath);
        op.get("operation-headers", "allow-resource-service-restart").set("true");
        client.getControllerClient().execute(op);

        // WildFly
        op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("core-service", "management");
        op.get("address").add("security-realm", "SslRealm");
        client.getControllerClient().execute(op);

        op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("core-service", "management");
        op.get("address").add("security-realm", "SslRealm");
        op.get("address").add("server-identity", "ssl");
        op.get("keystore-password").set(keystorePassword);
        op.get("keystore-path").set(keystorePath);
        op.get("operation-headers", "allow-resource-service-restart").set("true");
        client.getControllerClient().execute(op);

        op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("subsystem", "undertow");
        op.get("address").add("server", "default-server");
        op.get("address").add("https-listener", "default-https");
        op.get("socket-binding").set("https");
        op.get("security-realm").set("SslRealm");
        op.get("operation-headers", "allow-resource-service-restart").set("true");
        client.getControllerClient().execute(op);
    }

    public static void tearDownSSL(ManagementClient client) throws Exception {
        // EAP
        ModelNode op = new ModelNode();
        op.get("operation").set("remove");
        op.get("address").add("subsystem", "web");
        op.get("address").add("connector", "https");
        client.getControllerClient().execute(op);

        // WildFly
        op = new ModelNode();
        op.get("operation").set("remove");
        op.get("address").add("subsystem", "undertow");
        op.get("address").add("server", "default-server");
        op.get("address").add("https-listener", "https");
        client.getControllerClient().execute(op);

        op = new ModelNode();
        op.get("operation").set("remove");
        op.get("address").add("core-service", "management");
        op.get("address").add("security-realm", "SslRealm");
        client.getControllerClient().execute(op);
    }

    private static ModelControllerClient createClient(final String host, final int port) throws UnknownHostException {
        return ModelControllerClient.Factory.create(InetAddress.getByName(host), port);
    }

}
