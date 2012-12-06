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
package org.switchyard.component.camel.ftp.model.v1;

import static org.switchyard.component.camel.ftp.Constants.FTP_NAMESPACE_V1;

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
     * Default constuctor. Creates CamelFtpBindingModel.
     */
    public V1CamelFtpBindingModel() {
        this(FTP, FTP_NAMESPACE_V1);
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
