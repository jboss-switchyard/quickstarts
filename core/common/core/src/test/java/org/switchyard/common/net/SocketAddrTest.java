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

package org.switchyard.common.net;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for SocketAddr.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SocketAddrTest {

    @Test
    public void socketNames() throws Exception {
        SocketAddr socket = new SocketAddr("localhost:18001");
        Assert.assertNotNull(socket);
        Assert.assertEquals(socket.getHost(), "localhost");
        Assert.assertEquals(socket.getPort(), 18001);
        SocketAddr socket1 = new SocketAddr("localhost", 18001);
        Assert.assertEquals(socket, socket1);
        Assert.assertEquals(socket.hashCode(), socket1.hashCode());
        Assert.assertEquals(socket.toString(), socket1.toString());
        socket = new SocketAddr("127.0.0.1:");
        Assert.assertNotNull(socket);
        Assert.assertEquals(socket.getHost(), "127.0.0.1");
        Assert.assertEquals(socket.getPort(), SocketAddr.DEFAULT_PORT);
        Assert.assertNotSame(socket, socket1);
        Assert.assertNotSame(socket.hashCode(), socket1.hashCode());
        Assert.assertNotSame(socket.toString(), socket1.toString());
        socket = new SocketAddr(":18001");
        Assert.assertNotNull(socket);
        Assert.assertEquals(socket.getHost(), SocketAddr.DEFAULT_HOST);
        Assert.assertEquals(socket.getPort(), 18001);
        socket = new SocketAddr();
        Assert.assertEquals(socket.getHost(), SocketAddr.DEFAULT_HOST);
        Assert.assertEquals(socket.getPort(), SocketAddr.DEFAULT_PORT);
        socket = new SocketAddr("");
        Assert.assertEquals(socket.getHost(), SocketAddr.DEFAULT_HOST);
        Assert.assertEquals(socket.getPort(), SocketAddr.DEFAULT_PORT);
    }
}
