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
package org.switchyard.component.camel.ftps.model.v1;

import static org.switchyard.component.camel.ftp.Constants.FTP_NAMESPACE_V1;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.ftp.model.v1.V1CamelFtpBindingModel;
import org.switchyard.component.camel.ftps.model.CamelFtpsBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of ftps configuration binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelFtpsBindingModel extends V1CamelFtpBindingModel implements
    CamelFtpsBindingModel {

    /**
     * Ftps endpoint prefix.
     */
    public static final String FTPS = "ftps";

    private static final String SECURITY_PROTOCOL = "securityProtocol";
    private static final String IMPLICT = "isImplicit";
    private static final String EXEC_PBSZ = "execPbsz";
    private static final String EXEC_PROT = "execProt";
    private static final String DISABLE_SECURE_DATA_CHANNEL_DEFAULTS = "disableSecureDataChannelDefaults";

    /**
     * Create a CamelFtpBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelFtpsBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    /**
     * Default constuctor. Creates CamelFtpBindingModel.
     */
    public V1CamelFtpsBindingModel() {
        super(FTPS, FTP_NAMESPACE_V1);

        setModelChildrenOrder(SECURITY_PROTOCOL, IMPLICT, EXEC_PBSZ, EXEC_PROT, DISABLE_SECURE_DATA_CHANNEL_DEFAULTS);
    }

    @Override
    protected String getEndpointProtocol() {
        return FTPS;
    }

    @Override
    public String getSecurityProtocol() {
        return getConfig(SECURITY_PROTOCOL);
    }

    @Override
    public V1CamelFtpsBindingModel setSecurityProtocol(String protocol) {
        return setConfig(SECURITY_PROTOCOL, protocol);
    }

    @Override
    public Boolean isImplict() {
        return getBooleanConfig(IMPLICT);
    }

    @Override
    public V1CamelFtpsBindingModel setImplict(Boolean implict) {
        return setConfig(IMPLICT, String.valueOf(implict));
    }

    @Override
    public Long getExecPbsz() {
        return getLongConfig(EXEC_PBSZ);
    }

    @Override
    public V1CamelFtpsBindingModel setExecPbsz(Long pbsz) {
        return setConfig(EXEC_PBSZ, String.valueOf(pbsz));
    }

    @Override
    public String getExecProt() {
        return getConfig(EXEC_PROT);
    }

    @Override
    public V1CamelFtpsBindingModel setExecProt(String prot) {
        return setConfig(EXEC_PROT, prot);
    }

    @Override
    public Boolean isDisableSecureDataChannelDefaults() {
        return getBooleanConfig(DISABLE_SECURE_DATA_CHANNEL_DEFAULTS);
    }

    @Override
    public V1CamelFtpsBindingModel setDisableSecureDataChannelDefaults(Boolean disableDefaults) {
        return setConfig(DISABLE_SECURE_DATA_CHANNEL_DEFAULTS, String.valueOf(disableDefaults));
    }

    @Override
    protected void enrichQueryString(QueryString queryString) {
        // nothing to do here..
    }

}
