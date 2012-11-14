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
package org.switchyard.component.camel.config.model.mail.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.mail.CamelMailProducerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModel;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * First implementation of mail producer binding.
 */
public class V1CamelMailProducerBindingModel extends V1BaseCamelModel 
    implements CamelMailProducerBindingModel {

    /**
     * Camel endpoint type used for mail producers.
     */
    private static final String SMTP = "smtp";

    private static final String SUBJECT = "subject";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String CC = "CC";
    private static final String BCC = "BCC";
    private static final String REPLY_TO = "replyTo";

    /**
     * Creates new producer binding model.
     */
    public V1CamelMailProducerBindingModel() {
        super(new QName(V1CamelBindingModel.PRODUCE));

        setModelChildrenOrder(SUBJECT, FROM, TO, CC, BCC, REPLY_TO);
    }

    /**
     * Creates new producer binding model.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelMailProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getSubject() {
        return getConfig(SUBJECT);
    }

    @Override
    public V1CamelMailProducerBindingModel setSubject(String subject) {
        return setConfig(SUBJECT, subject);
    }

    @Override
    public String getFrom() {
        return getConfig(FROM);
    }

    @Override
    public V1CamelMailProducerBindingModel setFrom(String from) {
        return setConfig(FROM, from);
    }

    @Override
    public String getTo() {
        return getConfig(TO);
    }

    @Override
    public V1CamelMailProducerBindingModel setTo(String to) {
        return setConfig(TO, to);
    }

    @Override
    public String getCC() {
        return getConfig(CC);
    }

    @Override
    public V1CamelMailProducerBindingModel setCC(String cc) {
        return setConfig(CC, cc);
    }

    @Override
    public String getBCC() {
        return getConfig(BCC);
    }

    @Override
    public V1CamelMailProducerBindingModel setBCC(String bcc) {
        return setConfig(BCC, bcc);
    }

    @Override
    public String getReplyTo() {
        return getConfig(REPLY_TO);
    }

    @Override
    public V1CamelMailProducerBindingModel setReplyTo(String replyTo) {
        return setConfig(REPLY_TO, replyTo);
    }

    @Override
    public String getProtocol() {
        return SMTP;
    }

}
