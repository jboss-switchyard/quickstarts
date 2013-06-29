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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    public static final String DEFAULT_REALM = "ApplicationRealm";
    public static final String APPLICATION_ROLES_PROPERTIES = "application-roles.properties";
    public static final String USER = "guest";
    public static final String PASSWD = "Guestp123!";
    
    private ResourceDeployer() {
    }
    
    public static ModelNode addQueue(final String host, final int port, final String queueName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        final ModelNode op = new ModelNode();
        op.get("operation").set("add");
        op.get("address").add("subsystem", "messaging");
        op.get("address").add("hornetq-server", "default");
        op.get("address").add("jms-queue", queueName);
        op.get("entries").add(queueName)
                         .add("java:jboss/exported/jms/" + queueName);

        return client.execute(op);
    }

    public static ModelNode addQueue(final String queueName) throws IOException {
        return addQueue(DEFAULT_HOST, DEFAULT_PORT, queueName);
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

    public static void addPropertiesUser() throws IOException {
        addPropertiesUser(USER, PASSWD);
    }

    public static void addPropertiesUser(String user, String passwd) throws IOException {
        try {
            org.jboss.as.domain.management.security.AddPropertiesUser.main(
                new String[]{"--silent",
                             "-a",
                             "--role",
                             "guest",
                             user,
                             passwd,
                             DEFAULT_REALM});
        } catch (RuntimeException re) {
            if (!re.getMessage().contains("JBAS015243")) {
                throw re;
            }
        }
        
        /* https://issues.jboss.org/browse/AS7-3950 AddPropertiesUser should do this but doesn't right now
        String home = System.getenv("JBOSS_HOME");
        File roleFile = new File(home + "/standalone/configuration/" + APPLICATION_ROLES_PROPERTIES);
        BufferedReader read = new BufferedReader(new FileReader(roleFile));
        String line = null;
        while ((line = read.readLine()) != null) {
            if (line.trim().startsWith("user")) {
                read.close();
                return;
            }
        }
        
        BufferedWriter write = new BufferedWriter(new FileWriter(roleFile,true));
        write.newLine();
        write.append(user + "=guest");
        write.close();*/
    }
    
    private static ModelControllerClient createClient(final String host, final int port) throws UnknownHostException {
        return ModelControllerClient.Factory.create(InetAddress.getByName(host), port);
    }

}
