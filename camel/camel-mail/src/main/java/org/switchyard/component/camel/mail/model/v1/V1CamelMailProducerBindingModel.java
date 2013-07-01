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
package org.switchyard.component.camel.mail.model.v1;

import static org.switchyard.component.camel.mail.model.Constants.MAIL_NAMESPACE_V1;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelModel;
import org.switchyard.component.camel.mail.model.CamelMailProducerBindingModel;
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
        super(V1CamelMailBindingModel.PRODUCE, MAIL_NAMESPACE_V1);

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
