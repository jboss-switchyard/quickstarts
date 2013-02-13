/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
