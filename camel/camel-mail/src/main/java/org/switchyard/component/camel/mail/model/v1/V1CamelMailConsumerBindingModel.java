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

import org.switchyard.component.camel.common.model.v1.V1CamelScheduledBatchPollConsumer;
import org.switchyard.component.camel.mail.model.CamelMailConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * First implementation of mail consumer/receiver binding.
 */
public class V1CamelMailConsumerBindingModel extends V1CamelScheduledBatchPollConsumer
    implements CamelMailConsumerBindingModel {

    /**
     * Enumeration type representing supported mail server types.
     */
    public enum AccountType {
        /**
         * Imap based backend.
         */
        imap,
        /**
         * POP3 based backend.
         */
        pop3;
    }

    private static final String ACCOUNT_TYPE = "accountType";
    private static final String FOLDER_NAME = "folderName";
    private static final String FETCH_SIZE = "fetchSize";
    private static final String UNSEEN = "unseen";
    private static final String DELETE = "delete";
    private static final String COPY_TO = "copyTo";
    private static final String DISCONNECT = "disconnect";

    /**
     * Creates new consumer binding model.
     */
    public V1CamelMailConsumerBindingModel() {
        super(V1CamelMailBindingModel.CONSUME, MAIL_NAMESPACE_V1);

        setModelChildrenOrder(FOLDER_NAME, FETCH_SIZE, UNSEEN, DELETE, COPY_TO, DISCONNECT);
    }

    /**
     * Creates new consumer binding model.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelMailConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getFolderName() {
        return getConfig(FOLDER_NAME);
    }

    @Override
    public V1CamelMailConsumerBindingModel setFolderName(String folderName) {
        return setConfig(FOLDER_NAME, folderName);
    }

    @Override
    public Integer getFetchSize() {
        return getIntegerConfig(FETCH_SIZE);
    }

    @Override
    public V1CamelMailConsumerBindingModel setFetchSize(Integer fetchSize) {
        return setConfig(FETCH_SIZE, fetchSize);
    }

    @Override
    public Boolean isUnseen() {
        return getBooleanConfig(UNSEEN);
    }

    @Override
    public V1CamelMailConsumerBindingModel setUnseen(Boolean unseen) {
        return setConfig(UNSEEN, unseen);
    }

    @Override
    public Boolean isDelete() {
        return getBooleanConfig(DELETE);
    }

    @Override
    public V1CamelMailConsumerBindingModel setDelete(Boolean delete) {
        return setConfig(DELETE, delete);
    }

    @Override
    public String getCopyTo() {
        return getConfig(COPY_TO);
    }

    @Override
    public V1CamelMailConsumerBindingModel setCopyTo(String copyTo) {
        return setConfig(COPY_TO, copyTo);
    }

    @Override
    public Boolean isDisconnect() {
        return getBooleanConfig(DISCONNECT);
    }

    @Override
    public V1CamelMailConsumerBindingModel setDisconnect(Boolean disconnect) {
        return setConfig(DISCONNECT, disconnect);
    }

    @Override
    public V1CamelMailConsumerBindingModel setAccountType(String accountType) {
        setModelAttribute(ACCOUNT_TYPE, accountType);
        return this;
    }

    @Override
    public String getProtocol() {
        AccountType accountType = getModelConfiguration() != null
            ? AccountType.valueOf(getModelAttribute(ACCOUNT_TYPE))
            : AccountType.imap;
        return accountType.name();
    }

}
