/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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
 * @author Daniel Bevenius
 *
 */
public class ResourceDeployer {
    
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 9999;
    
    private ResourceDeployer() {
    }
    
    public static ModelNode addQueue(final String host, final int port, final String queueName) throws IOException {
        final ModelControllerClient client = createClient(host, port);
        final ModelNode op = new ModelNode();
        op.get("address").add("subsystem", "messaging").add("jms-queue", queueName);
        op.get("entries").add("queue/" + queueName);
        op.get("operation").set("add");
        return client.execute(op);
    }

    public static ModelNode addQueue(final String queueName) throws IOException {
        return addQueue(DEFAULT_HOST, DEFAULT_PORT, queueName);
    }
    
    private static ModelControllerClient createClient(final String host, final int port) throws UnknownHostException {
        return ModelControllerClient.Factory.create(InetAddress.getByName(host), port);
    }

}
