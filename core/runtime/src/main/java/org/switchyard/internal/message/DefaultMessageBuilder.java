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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.message.DefaultMessage;

public class DefaultMessageBuilder extends MessageBuilder {
    
    public static final String TYPE = 
        "org.switchyard.messageType.default";
    
    @Override
    public Message buildMessage() {
        return new DefaultMessage();
    }
    
    @Override
    public Message readMessage(InputStream in) 
            throws IOException {
        // TODO : ignoring attachments at the moment
        Message msg = new DefaultMessage();
        ObjectInputStream ois = new ObjectInputStream(in);
        try {
            msg.setContent(ois.readObject());
        }
        catch (ClassNotFoundException cnfEx) {
            throw new RuntimeException(
                    "Failed to load content class for message", cnfEx);
        }
        
        return msg;
    }

    @Override
    public void writeMessage(Message message, OutputStream out)
            throws IOException {
        // TODO : ignoring attachments at the moment
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(message.getContent());
        oos.flush();
    }
    

}
