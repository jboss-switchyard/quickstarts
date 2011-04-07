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
