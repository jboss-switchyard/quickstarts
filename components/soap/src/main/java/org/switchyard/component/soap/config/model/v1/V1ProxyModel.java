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
package org.switchyard.component.soap.config.model.v1;

import org.switchyard.component.soap.config.model.ProxyModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel.SOAPName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A ProxyModel V1 implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1ProxyModel extends BaseModel implements ProxyModel {

    private static final String[] MODEL_CHILDREN_ORDER = new String[]{
        SOAPName.type.name(),
        SOAPName.host.name(),
        SOAPName.port.name(),
        SOAPName.user.name(),
        SOAPName.password.name()
    };

    private static final String DEFAULT_PROXY_TYPE = "HTTP";

    private SOAPNameValueModel _type;
    private SOAPNameValueModel _host;
    private SOAPNameValueModel _port;
    private SOAPNameValueModel _user;
    private SOAPNameValueModel _password;

    /**
     * Creates a new ProxyModel.
     * @param namespace namespace
     */
    public V1ProxyModel(String namespace) {
        super(namespace, SOAPName.proxy.name());
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new ProxyModel.
     * @param namespace namespace
     * @param name the name of the model
     */
    public V1ProxyModel(String namespace, String name) {
        super(namespace, name);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new ProxyModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ProxyModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }


    /**
     * {@inheritDoc}
     */
    public String getType() {
        if (_type == null) {
            _type = getNameValue(SOAPName.type);
        }
        return _type != null ? _type.getValue() : DEFAULT_PROXY_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setType(String type) {
        _type = setNameValue(_type, SOAPName.type, type);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        if (_host == null) {
            _host = getNameValue(SOAPName.host);
        }
        return _host != null ? _host.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setHost(String host) {
        _host = setNameValue(_host, SOAPName.host, host);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getPort() {
        if (_port == null) {
            _port = getNameValue(SOAPName.port);
        }
        return _port != null ? _port.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        if (_user == null) {
            _user = getNameValue(SOAPName.user);
        }
        return _user != null ? _user.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setUser(String user) {
        _user = setNameValue(_user, SOAPName.user, user);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        if (_password == null) {
            _password = getNameValue(SOAPName.password);
        }
        return _password != null ? _password.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setPassword(String password) {
        _password = setNameValue(_password, SOAPName.password, password);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setPort(String port) {
        _port = setNameValue(_port, SOAPName.port, port);
        return this;
    }

    protected SOAPNameValueModel getNameValue(SOAPName name) {
        return (SOAPNameValueModel)getFirstChildModel(name.name());
    }

    protected SOAPNameValueModel setNameValue(SOAPNameValueModel model, SOAPName name, String value) {
        if (value != null) {
            if (model == null) {
                model = getNameValue(name);
            }
            if (model == null) {
                model = new V1SOAPNameValueModel(getNamespaceURI(), name);
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
