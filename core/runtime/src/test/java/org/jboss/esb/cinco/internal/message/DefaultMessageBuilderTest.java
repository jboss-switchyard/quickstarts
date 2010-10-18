/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.esb.cinco.internal.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.Assert;

import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.MessageBuilder;
import org.junit.Before;
import org.junit.Test;

public class DefaultMessageBuilderTest {
	
	private MessageBuilder _builder;
	
	@Before
	public void setUp() throws Exception {
		_builder = MessageBuilder.newInstance();
	}
	
	@Test
	public void testReadWriteMessage() throws Exception {
		//create a message and fill it with some stuff
		Message origMsg =_builder.buildMessage();
		origMsg.setContent("howdy folks!");
		
		// create a stream sink and write the message to it
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		_builder.writeMessage(origMsg, bos);
		bos.flush();
		
		// now try and read the message back in and compare it to the orig
		Message newMsg = _builder.readMessage(
				new ByteArrayInputStream(bos.toByteArray()));
		Assert.assertNotNull(newMsg);
		Assert.assertEquals(origMsg.getContent(), newMsg.getContent());
	}
	
}
