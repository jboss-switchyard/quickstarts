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
package org.switchyard.component.camel.config.model.ftp.v1;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.ftp.CamelFtpBindingModel;
import org.switchyard.component.camel.config.model.remote.CamelRemoteFileBindingModel;
import org.switchyard.component.camel.config.model.remote.v1.V1CamelRemoteFileBindingModel;
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
    protected static final String SOCKET_TIMEOUT = "soTimeout";

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

        setModelChildrenOrder(PASSIVE_MODE, TIMEOUT, SOCKET_TIMEOUT, SITE_COMMAND);
    }

    /**
     * Default constuctor. Creates CamelFtpBindingModel.
     */
    public V1CamelFtpBindingModel() {
        this(FTP);
    }

    /**
     * Creates ftp binding model with different protocol scheme.
     * 
     * @param protocol Protocol scheme.
     */
    public V1CamelFtpBindingModel(String protocol) {
        super(protocol);
    }

    @Override
    public Boolean isPassiveMode() {
        return getBooleanConfig(PASSIVE_MODE);
    }

    @Override
    public CamelRemoteFileBindingModel setPassiveMode(boolean passive) {
        return setConfig(PASSIVE_MODE, String.valueOf(passive));
    }

    @Override
    public Integer getTimeout() {
        return getIntegerConfig(TIMEOUT);
    }

    @Override
    public CamelRemoteFileBindingModel setTimeout(int timeout) {
        return setConfig(TIMEOUT, String.valueOf(timeout));
    }

    @Override
    public Integer getSocketTimeout() {
        return getIntegerConfig(SOCKET_TIMEOUT);
    }

    @Override
    public CamelRemoteFileBindingModel setSocketTimeout(int timeout) {
        return setConfig(SOCKET_TIMEOUT, String.valueOf(timeout));
    }

    @Override
    public String getSiteCommand() {
        return getConfig(SITE_COMMAND);
    }

    @Override
    public CamelRemoteFileBindingModel setSiteCommand(String command) {
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
