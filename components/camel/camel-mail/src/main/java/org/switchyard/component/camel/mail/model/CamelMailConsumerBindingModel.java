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
