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
     * @return the message context
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
     */
    void removeAttachment(String name);

    /**
     * Returns a map containing all attachments to this message. The returned
     * map is not a live reference to the underlying attachment map in the
     * Message so changes to the returned map will not be reflected in the
     * Message instance.
     * @return a map containing all message attachments
     */
    Map<String, DataSource> getAttachmentMap();

    /**
     * Return copy of message. The copy will contain clean context and point
     * to same payload object. In other words contents of message is not cloned
     * nor copied.
     *
     * @return copy of message.
     */
    Message copy();

}
