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

import org.switchyard.component.camel.config.model.mail.CamelMailConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelScheduledBatchPollConsumer;
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
        super(new QName(V1BaseCamelBindingModel.CONSUME));

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
        String modelAttribute = getModelAttribute(ACCOUNT_TYPE);
        AccountType accountType = modelAttribute != null ? AccountType.valueOf(modelAttribute) : AccountType.imap;
        return accountType.name();
    }

}
