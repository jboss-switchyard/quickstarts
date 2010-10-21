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

package org.switchyard.internal.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.EsbException;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.message.StreamMessage;

public class StreamMessageBuilder extends MessageBuilder {
	
	public static final String TYPE = 
		"org.switchyard.messageType.stream";
	private static final int WRITE_BUFFER = 8 * 1024;
	
	@Override
	public StreamMessage buildMessage() {
		return new StreamMessage();
	}
	
	@Override
	public StreamMessage readMessage(InputStream in) 
			throws IOException, EsbException {
		// TODO : ignoring attachments at the moment
		StreamMessage msg = new StreamMessage();
		msg.setContent(in);
		return msg;
	}

	@Override
	public void writeMessage(Message message, OutputStream out)
			throws IOException, EsbException {
		if (!(message instanceof StreamMessage)) {
			throw new EsbException(
					"Invalid message type for StreamBuilder writeMessage: " +
					message.getClass().getName());
		}
		writeMessage((StreamMessage)message, out);
	}
	
	public void writeMessage(StreamMessage message, OutputStream out) 
			throws IOException, EsbException {
		// TODO : ignoring attachments at the moment
		byte[] buf = new byte[WRITE_BUFFER];
		int count;
		InputStream in = message.getContent();
		while ((count = in.read(buf)) != -1) {
			out.write(buf, 0, count);
		}
		out.flush();
	}

}
