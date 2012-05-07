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
package org.switchyard.component.camel.config.model.remote.v1;

import java.net.URI;
import java.util.List;

import org.apache.camel.component.file.remote.RemoteFileConfiguration;
import org.apache.camel.component.file.remote.RemoteFileConfiguration.PathSeparator;
import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.remote.CamelRemoteFileBindingModel;
import org.switchyard.component.camel.config.model.remote.CamelRemoteFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Base class for remote file endpoint configurations.
 * 
 * @author Lukasz Dywicki
 */
public abstract class V1CamelRemoteFileBindingModel extends V1BaseCamelBindingModel
    implements CamelRemoteFileBindingModel {

    /**
     * Name of 'directory' element.
     */
    protected static final String DIRECTORY = "directory";

    /**
     * Name of 'username' element.
     */
    protected static final String USERNAME = "username";

    /**
     * Name of 'host' element.
     */
    protected static final String HOST = "host";

    /**
     * Name of 'port' element.
     */
    protected static final String PORT = "port";

    /**
     * Name of 'password' element.
     */
    protected static final String PASSWORD = "password";

    /**
     * Name of 'binary' element.
     */
    protected static final String BINARY = "binary";

    /**
     * Name of 'connectTimeout' element.
     */
    protected static final String CONNECTION_TIMEOUT = "connectTimeout";

    /**
     * Name of 'throwExceptionOnConnectFailed' element.
     */
    protected static final String THROW_EXCEPTION_ON_CONNECTION_FAILED = "throwExceptionOnConnectFailed";

    /**
     * Name of 'stepwise' element.
     */
    protected static final String STEPWISE = "stepwise";

    /**
     * Name of 'separator' element.
     */
    protected static final String SEPARATOR = "separator";

    /**
     * Name of 'consume' element.
     */
    protected static final String CONSUME = "consume";

    /**
     * Remote file consumer.
     */
    private CamelRemoteFileConsumerBindingModel _consume;

    /**
     * Create a CamelRemoteBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelRemoteFileBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    /**
     * Creates CamelRemoteBindingModel with given protocol name.
     * 
     * @param protocol Protocol prefix in uri.
     */
    protected V1CamelRemoteFileBindingModel(String protocol) {
        super(protocol);

        setModelChildrenOrder(
            USERNAME, HOST, PORT, DIRECTORY, PASSWORD, BINARY,
            CONNECTION_TIMEOUT, THROW_EXCEPTION_ON_CONNECTION_FAILED,
            STEPWISE, SEPARATOR, CONSUME
        );
    }

    @Override
    public String getUsername() {
        return getConfig(USERNAME);
    }

    @Override
    public CamelRemoteFileBindingModel setUsername(String username) {
        return setConfig(USERNAME, username);
    }

    @Override
    public String getHost() {
        return getConfig(HOST);
    }

    @Override
    public CamelRemoteFileBindingModel setHost(String host) {
        return setConfig(HOST, host);
    }

    @Override
    public Integer getPort() {
        return getIntegerConfig(PORT);
    }

    @Override
    public CamelRemoteFileBindingModel setPort(int port) {
        return setConfig(PORT, String.valueOf(port));
    }

    @Override
    public String getDirectory() {
        return getConfig(DIRECTORY);
    }

    @Override
    public CamelRemoteFileBindingModel setDirectory(String directory) {
        return setConfig(DIRECTORY, directory);
    }

    @Override
    public String getPassword() {
        return getConfig(PASSWORD);
    }

    @Override
    public CamelRemoteFileBindingModel setPassword(String password) {
        return setConfig(PASSWORD, password);
    }

    @Override
    public Boolean isBinary() {
        return getBooleanConfig(BINARY);
    }

    @Override
    public CamelRemoteFileBindingModel setBinary(boolean binary) {
        return setConfig(BINARY, String.valueOf(binary));
    }

    @Override
    public Integer getConnectTimeout() {
        return getIntegerConfig(CONNECTION_TIMEOUT);
    }

    @Override
    public CamelRemoteFileBindingModel setConnectionTimeout(int timeout) {
        return setConfig(CONNECTION_TIMEOUT, String.valueOf(timeout));
    }

    @Override
    public Boolean isThrowExceptionOnConnectionFailed() {
        return getBooleanConfig(THROW_EXCEPTION_ON_CONNECTION_FAILED);
    }

    @Override
    public CamelRemoteFileBindingModel setThrowExceptionOnConnectionFailed(boolean throwException) {
        return setConfig(THROW_EXCEPTION_ON_CONNECTION_FAILED, String.valueOf(throwException));
    }

    @Override
    public Boolean isStepwise() {
        return getBooleanConfig(STEPWISE);
    }

    @Override
    public CamelRemoteFileBindingModel setStepwise(boolean stepwise) {
        return setConfig(STEPWISE, String.valueOf(stepwise));
    }

    @Override
    public PathSeparator getSeparator() {
        return getEnumerationConfig(SEPARATOR, RemoteFileConfiguration.PathSeparator.class);
    }

    @Override
    public CamelRemoteFileBindingModel setSeparator(String separator) {
        return setConfig(SEPARATOR, separator);
    }
    @Override
    public CamelRemoteFileConsumerBindingModel getConsumer() {
        if (_consume == null) {
            Configuration config = getModelConfiguration().getFirstChild(CONSUME);
            _consume = new V1CamelRemoteFileConsumerBindingModel(config,
                getModelDescriptor());
        }
        return _consume;
    }

    @Override
    public V1CamelRemoteFileBindingModel setConsumer(CamelRemoteFileConsumerBindingModel consumer) {
        Configuration config = getModelConfiguration().getFirstChild(CONSUME);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(CONSUME);
            getModelConfiguration().addChild(((V1CamelRemoteFileConsumerBindingModel) consumer)
                .getModelConfiguration());
        } else {
            setChildModel((V1CamelRemoteFileConsumerBindingModel) consumer);
        }
        _consume = consumer;
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String username = getUsername() != null ? getUsername() + "@" : "";
        String baseUri = getEndpointProtocol() + "://" + username + getHost();
        baseUri += getPort() != null ? ":" + getPort() : "";
        baseUri += getDirectory() != null ? "/" + getDirectory() : "";

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, HOST, PORT, USERNAME, DIRECTORY);

        return URI.create(baseUri + queryStr.toString());
    }

    protected abstract String getEndpointProtocol();
    protected abstract void enrichQueryString(QueryString queryString);
}
