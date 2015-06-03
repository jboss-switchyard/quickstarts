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

package org.switchyard.component.http.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.http.config.model.BasicAuthModel;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.http.config.model.HttpNameValueModel;
import org.switchyard.component.http.config.model.HttpNameValueModel.HttpName;
import org.switchyard.component.http.config.model.NtlmAuthModel;
import org.switchyard.component.http.config.model.ProxyModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * A HTTP binding model.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1HttpBindingModel extends V1BindingModel implements HttpBindingModel {

    private static final String[] MODEL_CHILDREN_ORDER = new String[]{
        HttpName.address.name(),
        HttpName.contextPath.name(),
        HttpName.method.name(),
        HttpName.contentType.name(),
        HttpName.timeout.name(),
        HttpName.basic.name(),
        HttpName.ntlm.name(),
        HttpName.proxy.name()
    };

    private Configuration _environment;
    private QName _serviceName;

    private HttpNameValueModel _wsdl;
    private HttpNameValueModel _contextPath;
    private HttpNameValueModel _address;
    private HttpNameValueModel _method;
    private HttpNameValueModel _contentType;
    private HttpNameValueModel _timeout;
    private BasicAuthModel _basicAuth;
    private NtlmAuthModel _ntlmAuth;
    private ProxyModel _proxyConfig;

    /**
     * Constructor.
     * @param namespace namespace
     */
    public V1HttpBindingModel(String namespace) {
        super(HTTP, namespace);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Create a V1HttpBindingModel using configuration and descriptor.
     * 
     * @param config the HttpGateway configuration
     * @param desc the HttpGateway descriptor
     */
    public V1HttpBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
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
    public HttpBindingModel setServiceName(QName serviceName) {
        _serviceName = serviceName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getContextPath() {
        if (_contextPath == null) {
            _contextPath = getNameValue(HttpName.contextPath);
        }
        return _contextPath != null ? _contextPath.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setContextPath(String contextPath) {
        _contextPath = setNameValue(_contextPath, HttpName.contextPath, contextPath);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getAddress() {
        if (_address == null) {
            _address = getNameValue(HttpName.address);
        }
        return _address != null ? _address.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setAddress(String address) {
        _address = setNameValue(_address, HttpName.address, address);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getMethod() {
        if (_method == null) {
            _method = getNameValue(HttpName.method);
        }
        return _method != null ? _method.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setMethod(String method) {
        _method = setNameValue(_method, HttpName.method, method);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getContentType() {
        if (_contentType == null) {
            _contentType = getNameValue(HttpName.contentType);
        }
        return _contentType != null ? _contentType.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setContentType(String contentType) {
        _contentType = setNameValue(_contentType, HttpName.contentType, contentType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getTimeout() {
        if (_timeout == null) {
            _timeout = getNameValue(HttpName.timeout);
        }
        return _timeout != null ? Integer.valueOf(_timeout.getValue()) : null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setTimeout(Integer timeout) {
        _timeout = setNameValue(_timeout, HttpName.timeout, String.valueOf(timeout));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel getBasicAuthConfig() {
        if (_basicAuth == null) {
            _basicAuth = (BasicAuthModel)getFirstChildModel(HttpName.basic.name());
        }
        return _basicAuth;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setBasicAuthConfig(BasicAuthModel config) {
        setChildModel(config);
        _basicAuth = config;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public NtlmAuthModel getNtlmAuthConfig() {
        if (_ntlmAuth == null) {
            _ntlmAuth = (NtlmAuthModel)getFirstChildModel(HttpName.ntlm.name());
        }
        return _ntlmAuth;
    }

    /**
     * {@inheritDoc}
     */
    public HttpBindingModel setNtlmAuthConfig(NtlmAuthModel config) {
        setChildModel(config);
        _ntlmAuth = config;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyModel getProxyConfig() {
        if (_proxyConfig == null) {
            _proxyConfig = (ProxyModel)getFirstChildModel(HttpName.proxy.name());
        }
        return _proxyConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpBindingModel setProxyConfig(ProxyModel proxyConfig) {
        setChildModel(proxyConfig);
        _proxyConfig = proxyConfig;
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

    private HttpNameValueModel getNameValue(HttpName name) {
        return (HttpNameValueModel)getFirstChildModel(name.name());
    }

    private HttpNameValueModel setNameValue(HttpNameValueModel model, HttpName name, String value) {
        if (value != null) {
            if (model == null) {
                model = getNameValue(name);
            }
            if (model == null) {
                model = new V1HttpNameValueModel(getNamespaceURI(), name);
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
