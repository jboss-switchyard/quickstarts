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
import org.switchyard.component.camel.ftp.model.CamelSftpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Sftp protocol binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelSftpBindingModel extends V1CamelRemoteFileBindingModel
    implements CamelSftpBindingModel {

    /**
     * Sftp protocol/endpoint scheme.
     */
    public static final String SFTP = "sftp";

    private static final String KNOWN_HOSTS_FILE = "knownHostsFile";
    private static final String PRIVATE_KEY_FILE = "privateKeyFile";
    private static final String PRIVATE_KEY_FILE_PASSPHRASE = "privateKeyFilePassphrase";

    /**
     * Create a CamelSftpBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelSftpBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    /**
     * Create CamelSftpBindingModel.
     * @param namespace namespace
     */
    public V1CamelSftpBindingModel(String namespace) {
        super(SFTP, namespace);

        setModelChildrenOrder(KNOWN_HOSTS_FILE, PRIVATE_KEY_FILE, PRIVATE_KEY_FILE_PASSPHRASE,
            PRODUCE, CONSUME);
    }

    @Override
    public String getKnownHostsFile() {
        return getConfig(KNOWN_HOSTS_FILE);
    }

    @Override
    public V1CamelSftpBindingModel setKnownHostsFile(String knownHostsFile) {
        return setConfig(KNOWN_HOSTS_FILE, knownHostsFile);
    }

    @Override
    public String getPrivateKeyFile() {
        return getConfig(PRIVATE_KEY_FILE);
    }

    @Override
    public V1CamelSftpBindingModel setPrivateKeyFile(String privateKeyFile) {
        return setConfig(PRIVATE_KEY_FILE, privateKeyFile);
    }

    @Override
    public String getPrivateKeyFilePassphrase() {
        return getConfig(PRIVATE_KEY_FILE_PASSPHRASE);
    }

    @Override
    public V1CamelSftpBindingModel setPrivateKeyFilePassphrase(String passphrase) {
        return setConfig(PRIVATE_KEY_FILE_PASSPHRASE, passphrase);
    }

    @Override
    protected String getEndpointProtocol() {
        return SFTP;
    }

    @Override
    protected void enrichQueryString(QueryString queryString) {
        // nothing to do there
    }

}
