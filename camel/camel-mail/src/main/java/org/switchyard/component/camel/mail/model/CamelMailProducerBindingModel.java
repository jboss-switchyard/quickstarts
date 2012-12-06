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
