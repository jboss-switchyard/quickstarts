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

package org.jboss.esb.cinco;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.esb.cinco.internal.message.DefaultMessageBuilder;
import org.jboss.esb.cinco.message.Builder;

public abstract class MessageBuilder {

	public static final MessageBuilder newInstance() {
		return new DefaultMessageBuilder();
	}
	
	public static final MessageBuilder newInstance(
			Class<? extends Message> messageType) 
			throws EsbException {
		
		Builder builderInfo = messageType.getAnnotation(Builder.class);
		try {
			return builderInfo.value().newInstance();
		}
		catch (Exception ex) {
			throw new EsbException("Failed to load builder class for message", ex);
		}
	}
	
	public abstract Message buildMessage();

	public abstract void writeMessage(Message message, OutputStream out) 
		throws java.io.IOException, EsbException;
	public abstract Message readMessage(InputStream in)
		throws java.io.IOException, EsbException;
}
