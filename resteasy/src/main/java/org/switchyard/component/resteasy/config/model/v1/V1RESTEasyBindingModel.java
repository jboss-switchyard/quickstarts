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

package org.switchyard.component.resteasy.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.resteasy.config.model.BasicAuthModel;
import org.switchyard.component.resteasy.config.model.ContextParamsModel;
import org.switchyard.component.resteasy.config.model.NtlmAuthModel;
import org.switchyard.component.resteasy.config.model.ProxyModel;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel.RESTEasyName;
import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * A model that holds the RESTEasy gateway configuration V1.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1RESTEasyBindingModel extends V1BindingModel implements RESTEasyBindingModel {

    private static final String[] MODEL_CHILDREN_ORDER = new String[]{
        RESTEasyName.interfaces.name(),
        RESTEasyName.contextPath.name(),
        RESTEasyName.address.name(),
        RESTEasyName.timeout.name(),
        RESTEasyName.basic.name(),
        RESTEasyName.ntlm.name(),
        RESTEasyName.proxy.name(),
        ContextParamsModel.CONTEXT_PARAMS
    };

    private QName _serviceName;
    private Configuration _environment;

    private RESTEasyNameValueModel _address;
    private RESTEasyNameValueModel _interfaces;
    private RESTEasyNameValueModel _contextPath;
    private RESTEasyNameValueModel _timeout;
    private BasicAuthModel _basicAuth;
    private NtlmAuthModel _ntlmAuth;
    private ProxyModel _proxyConfig;

    /**
     * Constructor.
     * @param namespace namespace
     */
    public V1RESTEasyBindingModel(String namespace) {
        super(RESTEASY, namespace);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Create a RESTEasyBindingModel using configuration and descriptor.
     * 
     * @param config the RESTEasyGateway configuration
     * @param desc the RESTEasyGateway descriptor
     */
    public V1RESTEasyBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    public QName getServiceName() {
        if (_serviceName == null) {
            _serviceName = isServiceBinding() ? getService().getQName() : getReference().getQName();
        }
        return _serviceName;
    }

    /**
     * {@inheritDoc}
     */
    public void setServiceName(QName serviceName) {
        _serviceName = serviceName;
    }

    /**
     * {@inheritDoc}
     */
    public String getAddress() {
        if (_address == null) {
            _address = getNameValue(RESTEasyName.address);
        }
        return _address != null ? _address.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public RESTEasyBindingModel setAddress(String address) {
        _address = setNameValue(_address, RESTEasyName.address, address);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getInterfaces() {
        if (_interfaces == null) {
            _interfaces = getNameValue(RESTEasyName.interfaces);
        }
        return _interfaces != null ? _interfaces.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public RESTEasyBindingModel setInterfaces(String interfaces) {
        _interfaces = setNameValue(_interfaces, RESTEasyName.interfaces, interfaces);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getInterfacesAsArray() {
        String interfaces = getInterfaces();
        if (interfaces != null) {
            return interfaces.split(",");
        } else {
            return new String[0];
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getContextPath() {
        if (_contextPath == null) {
            _contextPath = getNameValue(RESTEasyName.contextPath);
        }
        return _contextPath != null ? _contextPath.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public RESTEasyBindingModel setContextPath(String contextPath) {
        _contextPath = setNameValue(_contextPath, RESTEasyName.contextPath, contextPath);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getTimeout() {
        if (_timeout == null) {
            _timeout = getNameValue(RESTEasyName.timeout);
        }
        return _timeout != null ? Integer.valueOf(_timeout.getValue()) : null;
    }

    /**
     * {@inheritDoc}
     */
    public RESTEasyBindingModel setTimeout(Integer timeout) {
        _timeout = setNameValue(_timeout, RESTEasyName.timeout, String.valueOf(timeout));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel getBasicAuthConfig() {
        if (_basicAuth == null) {
            _basicAuth = (BasicAuthModel)getFirstChildModel(RESTEasyName.basic.name());
        }
        return _basicAuth;
    }

    /**
     * {@inheritDoc}
     */
    public RESTEasyBindingModel setBasicAuthConfig(BasicAuthModel config) {
        setChildModel(config);
        _basicAuth = config;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public NtlmAuthModel getNtlmAuthConfig() {
        if (_ntlmAuth == null) {
            _ntlmAuth = (NtlmAuthModel)getFirstChildModel(RESTEasyName.ntlm.name());
        }
        return _ntlmAuth;
    }

    /**
     * {@inheritDoc}
     */
    public RESTEasyBindingModel setNtlmAuthConfig(NtlmAuthModel config) {
        setChildModel(config);
        _ntlmAuth = config;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isBasicAuth() {
        return (getBasicAuthConfig() != null) ? true : false;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean hasAuthentication() {
        return ((getBasicAuthConfig() != null) || (getNtlmAuthConfig() != null)) ? true : false;
    }

    @Override
    public ContextParamsModel getContextParamsConfig() {
        // introduced in v2
        return null;
    }

    @Override
    public RESTEasyBindingModel setContextParamsConfig(ContextParamsModel contextParamsConfig) {
        // introduced in v2
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyModel getProxyConfig() {
        if (_proxyConfig == null) {
            _proxyConfig = (ProxyModel)getFirstChildModel(RESTEasyName.proxy.name());
        }
        return _proxyConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingModel setProxyConfig(ProxyModel proxyConfig) {
        setChildModel(proxyConfig);
        _proxyConfig = proxyConfig;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getEnvironment() {
        if (_environment == null) {
            _environment = Configurations.newConfiguration();
        }
        return _environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingModel setEnvironment(Configuration environment) {
        _environment = environment;
        return this;
    }

    private RESTEasyNameValueModel getNameValue(RESTEasyName name) {
        return (RESTEasyNameValueModel)getFirstChildModel(name.name());
    }

    private RESTEasyNameValueModel setNameValue(RESTEasyNameValueModel model, RESTEasyName name, String value) {
        if (value != null) {
            if (model == null) {
                model = getNameValue(name);
            }
            if (model == null) {
                model = new V1RESTEasyNameValueModel(getNamespaceURI(), name);
                setChildModel(model);
            }
            model.setValue(value);
        } else {
            getModelConfiguration().removeChildren(name.name());
            model = null;
        }
        return model;
    }
}
