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

import java.util.Map;

import javax.activation.DataSource;

/**
 * A {@code Message} represents an individual input or output of a service, the
 * content of which is interpreted by service implementation logic.  A Message
 * does not carry context specific to a service invocation, which means that it
 * can be copied and reused across service invocations.
 * <p>
 * There are two distinct parts to a message:
 * <ul>
 * <li> <b>Content</b> : the main body, or payload, of the message. There is
 * only one body per message instance.  The message body is mutable, so message
 * processing logic can access the content, change it (transform, enrich, etc.),
 * and then set it back on the message.
 * <p>
 * <li> <b>Attachments</b> : provide the ability to associate content with a
 * message separate from  the main body, allowing it to be parsed independently.
 * One example  would be a binary image that is referenced by the main body of
 * the message.  The attachment may be too large to be processed in certain
 * services or the service implementation may not be able to  parse/interpret
 * it.
 * </ul>
 */
public interface Message {

    /**
     * Retrieves the message context.
     * @return the exchange context
     */
    Context getContext();
    
    /**
     * Assigns the specified content to the body of this message.
     *
     * @param content message body content
     * @return {@code this} message instance.
     */
    Message setContent(Object content);
    /**
     * Returns the content from the body of the message.
     * @return body content or null if the body has not been set
     */
    Object getContent();
    /**
     * Convenience method used to retrieve a typed instance of the message body.
     * @param <T> type
     * @param type body content type
     * @return body content or null if the body has not been set
     */
    <T> T getContent(Class<T> type);

    /**
     * Adds an attachment to the message with the specified name.
     * @param name attachment name
     * @param attachment attachment content
     * @return {@code this} message instance.
     */
    Message addAttachment(String name, DataSource attachment);
    /**
     * Retrieves the named attachment from the message.
     * @param name name of the attachment
     * @return the named attachment or null if the attachment does not exist
     */
    DataSource getAttachment(String name);
    /**
     * Removes the named attachment from this message.
     * @param name the attachment name
     * @return a reference to the removed attachment or null if the attachment
     * does not exist
     */
    DataSource removeAttachment(String name);
    /**
     * Returns a map containing all attachments to this message. The returned
     * map is not a live reference to the underlying attachment map in the
     * Message so changes to the returned map will not be reflected in the
     * Message instance.
     * @return a map containing all message attachments
     */
    Map<String, DataSource> getAttachmentMap();
}
