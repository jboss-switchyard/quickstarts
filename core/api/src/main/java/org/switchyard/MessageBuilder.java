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

package org.switchyard;

import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.message.Builder;
import org.switchyard.message.DefaultMessage;

/**
 * Extensible factory for {@code Message} instances. SwitchYard provides an 
 * Object-based MessageBuilder by default. Additional MessageBuilder can be
 * added as needed.
 */
public abstract class MessageBuilder {

    /**
     * Creates an instance of the default object-based MessageBuilder.
     * @return new MessageBuilder
     */
    public static final MessageBuilder newInstance() {
        return newInstance(DefaultMessage.class);
    }
    
    /**
     * Creates an instance of MessageBuilder to build the specified message type.
     * @param messageType type of Message you want to build
     * @return a new MessageBuilder instance
     */
    public static final MessageBuilder newInstance(
            final Class<? extends Message> messageType) {
        
        Builder builderInfo = messageType.getAnnotation(Builder.class);
        try {
            @SuppressWarnings("unchecked")
            Class<MessageBuilder> builderClass = 
                (Class<MessageBuilder>) Class.forName(builderInfo.value());
            
            return builderClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to load builder class for message", ex);
        }
    }
    
    /**
     * Creates a new Message instance.  Implementations of MessageBuilder 
     * implement this method and provide implementations consistent with the
     * type of the MessageBuilder.
     * @return a new Message instance
     */
    public abstract Message buildMessage();

    /**
     * Serializes the message instance to the specified output stream.
     * @param message message to serialize
     * @param out the stream to serialize to
     * @throws java.io.IOException serialization failed
     */
    public abstract void writeMessage(Message message, OutputStream out) 
        throws java.io.IOException;
    /**
     * Reads a message instance from the specified stream.
     * @param in the stream containing a message
     * @return a Message built from the contents of the input stream
     * @throws java.io.IOException deserialization failed
     */
    public abstract Message readMessage(InputStream in)
        throws java.io.IOException;
}
