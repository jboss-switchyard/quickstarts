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
package org.switchyard.component.camel.mail.model;

import org.switchyard.component.camel.common.model.consumer.CamelScheduledBatchPollConsumer;

/**
 * Mail consumer/receiver binding model.
 */
public interface CamelMailConsumerBindingModel extends CamelScheduledBatchPollConsumer {

    /**
     * The folder to poll.
     * 
     * @return Folder name.
     */
    String getFolderName();

    /**
     * Specify folder name to poll.
     * 
     * @param folderName Folder name to use for polling messages.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setFolderName(String folderName);

    /**
     * Sets the maximum number of messages to consume during a poll.
     * 
     * This can be used to avoid overloading a mail server, if a mailbox folder contains a lot of messages.
     * Default value of -1 means no fetch size and all messages will be consumed. Setting the value to 0
     * is a special corner case, where Camel will not consume any messages at all.
     *
     * @return Number of messages to read by one connection.
     */
    Integer getFetchSize();

    /**
     * Specify maximum number of messages for one poll.
     * 
     * @param fetchSize Number of messages to fetch.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setFetchSize(Integer fetchSize);

    /**
     * Should camel process only unseen messages (that is, new messages) or all messages.
     * 
     * Note that Camel always skips deleted messages. The default option of true will filter to only
     * unseen messages. This setting is applicable only for IMAP.
     *  
     * @return Should camel use SEEN flag for choosing messages to process.
     */
    Boolean isUnseen();

    /**
     * Specify to use SEEN flag with IMAP or not.
     * 
     * @param unseen True if camel should process only new (unseen) messages.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setUnseen(Boolean unseen);

    /**
     * Deletes the messages after they have been processed.
     * 
     * @return True if DELETED flag should be set on email after processing.
     */
    Boolean isDelete();

    /**
     * Specify if message should be deleted after processing.
     * 
     * @param delete True if camel should mark processed messages as deleted.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setDelete(Boolean delete);

    /**
     * After processing a mail message, it can be copied to a mail folder with the given name.
     * 
     * @return Name of folder to copy message after processing.
     */
    String getCopyTo();

    /**
     * Specify folder name to store copies of processed messages.
     * 
     * @param copyTo Folder name for storing copies.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setCopyTo(String copyTo);

    /**
     * Whether the consumer should disconnect after polling. If enabled this forces Camel to connect on each poll.
     * 
     * @return True if connection should be closed/opened for each poll.
     */
    Boolean isDisconnect();

    /**
     * Specify if connection should be closed after every poll.
     * 
     * @param disconnect Set to true if connection should be closed after poll.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setDisconnect(Boolean disconnect);

    /**
     * Get's protocol - pop3[s] or imap[s].
     * 
     * @return Protocol to use.
     */
    String getProtocol();

    /**
     * Specify mail server backend type.
     * 
     * @param accountType Type of server backend. Supported values are IMAP/POP3.
     * @return a reference to this Camel binding model
     */
    CamelMailConsumerBindingModel setAccountType(String accountType);

}
