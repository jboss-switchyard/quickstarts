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
package org.switchyard.component.hornetq.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.hornetq.core.client.impl.ClientMessageImpl;
import org.junit.Test;

/**
 * Unit test for {@link HornetQUtil}.
 * 
 * @author Daniel Bevenius
 *
 */
public class HornetQUtilTest {

    @Test
    public void readClientMessage() throws Exception {
        ClientMessageImpl clientMessage = new ClientMessageImpl((byte)0, false, 0, 0, (byte)4, 1500);
        clientMessage.getBodyBuffer().writeBytes("payload".getBytes());
        final Object result = HornetQUtil.readBytes(clientMessage);
        assertThat(result, is(instanceOf(byte[].class)));
        final String string = new String((byte[])result);
        assertThat(string, is(equalTo("payload")));
    }

}
