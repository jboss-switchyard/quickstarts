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

/**
 * Mail producer/sender binding model.
 */
public interface CamelMailProducerBindingModel {

    /**
     * Subject of the message being sent.
     * 
     * @return Mail subject.
     */
    String getSubject();

    /**
     * Specify mail subject to use.
     * 
     * @param subject Mail subject.
     * @return a reference to this Camel binding model
     */
    CamelMailProducerBindingModel setSubject(String subject);

    /**
     * The FROM email address.
     * 
     * @return Sender email address.
     */
    String getFrom();

    /**
     * Specify FROM email address.
     * 
     * @param from Sender email.
     * @return a reference to this Camel binding model
     */
    CamelMailProducerBindingModel setFrom(String from);

    /**
     * The TO recipients (the receivers of the mail). Separate multiple email addresses with a comma.
     * 
     * @return Mail recipients.
     */
    String getTo();

    /**
     * Specify mail recipients.
     * 
     * @param to Mail recipients.
     * @return a reference to this Camel binding model
     */
    CamelMailProducerBindingModel setTo(String to);

    /**
     * The CC recipients (the receivers of the mail). Separate multiple email addresses with a comma.
     * 
     * @return Mail recipients which should receive copy of sent mail.
     */
    String getCC();

    /**
     * Specify mail recipients.
     * 
     * @param cc Copy recipients.
     * @return a reference to this Camel binding model
     */
    CamelMailProducerBindingModel setCC(String cc);

    /**
     * The BCC recipients (the receivers of the mail). Separate multiple email addresses with a comma.
     * 
     * @return Mail recipients which should receive copy of sent mail but stay invisible for others recipients.
     */
    String getBCC();

    /**
     * Specify BCC recipients.
     * 
     * @param bcc Hidden mail recipients.
     * @return a reference to this Camel binding model
     */
    CamelMailProducerBindingModel setBCC(String bcc);

    /**
     * The Reply-To recipients (the receivers of the response mail). Separate multiple email addresses with a comma.
     * 
     * @return Reply to flag for mail.
     */
    String getReplyTo();

    /**
     * Specifies the addresses that should receive an answer for sent mail.
     * 
     * @param replyTo Reply-to addresses.
     * @return a reference to this Camel binding model
     */
    CamelMailProducerBindingModel setReplyTo(String replyTo);

    /**
     * Get protocol to use - smpt or smpts.
     * 
     * @return Protocol to use.
     */
    String getProtocol();

}
