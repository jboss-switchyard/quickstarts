/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.sftp.model.v1;

import static org.switchyard.component.camel.ftp.Constants.FTP_NAMESPACE_V1;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.remote.v1.V1CamelRemoteFileBindingModel;
import org.switchyard.component.camel.sftp.model.CamelSftpBindingModel;
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
     */
    public V1CamelSftpBindingModel() {
        super(SFTP, FTP_NAMESPACE_V1);

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
