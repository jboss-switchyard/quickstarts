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
package org.switchyard.component.camel.ftp.model.v1;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.remote.v1.V1CamelRemoteFileBindingModel;
import org.switchyard.component.camel.ftp.model.CamelFtpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of ftp configuration binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelFtpBindingModel extends V1CamelRemoteFileBindingModel implements
    CamelFtpBindingModel {

    /**
     * Ftp endpoint prefix.
     */
    public static final String FTP = "ftp";

    /**
     * Name of 'passiveMode' element.
     */
    protected static final String PASSIVE_MODE = "passiveMode";

    /**
     * Name of 'timeout' element.
     */
    protected static final String TIMEOUT = "timeout";

    /**
     * Name of 'soTimeout' element.
     */
    protected static final String SO_TIMEOUT = "soTimeout";

    /**
     * Name of 'siteCommand' element.
     */
    protected static final String SITE_COMMAND = "siteCommand";

    /**
     * Create a CamelFtpBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelFtpBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    /**
     * Default constructor. Creates CamelFtpBindingModel.
     * @param namespace namespace
     */
    public V1CamelFtpBindingModel(String namespace) {
        this(FTP, namespace);
    }

    /**
     * Creates ftp binding model with different protocol scheme.
     * 
     * @param protocol Protocol scheme.
     * @param namespace Namespace of extension.
     */
    protected V1CamelFtpBindingModel(String protocol, String namespace) {
        super(protocol, namespace);

        setModelChildrenOrder(PASSIVE_MODE, TIMEOUT, SO_TIMEOUT, SITE_COMMAND, CONSUME, PRODUCE);
    }

    @Override
    public Boolean isPassiveMode() {
        return getBooleanConfig(PASSIVE_MODE);
    }

    @Override
    public V1CamelFtpBindingModel setPassiveMode(boolean passive) {
        return setConfig(PASSIVE_MODE, passive);
    }

    @Override
    public Integer getTimeout() {
        return getIntegerConfig(TIMEOUT);
    }

    @Override
    public V1CamelFtpBindingModel setTimeout(int timeout) {
        return setConfig(TIMEOUT, timeout);
    }

    @Override
    public Integer getSoTimeout() {
        return getIntegerConfig(SO_TIMEOUT);
    }

    @Override
    public V1CamelFtpBindingModel setSoTimeout(int timeout) {
        return setConfig(SO_TIMEOUT, timeout);
    }

    @Override
    public String getSiteCommand() {
        return getConfig(SITE_COMMAND);
    }

    @Override
    public V1CamelFtpBindingModel setSiteCommand(String command) {
        return setConfig(SITE_COMMAND, command);
    }

    @Override
    protected void enrichQueryString(QueryString queryString) {
        // nothing to do here..
    }

    @Override
    protected String getEndpointProtocol() {
        return FTP;
    }

}
