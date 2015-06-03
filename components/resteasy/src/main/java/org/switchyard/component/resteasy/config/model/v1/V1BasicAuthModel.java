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

import org.switchyard.component.resteasy.config.model.BasicAuthModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel.RESTEasyName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A BasicAuthModel V1 implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1BasicAuthModel extends BaseModel implements BasicAuthModel {

    private static final String[] MODEL_CHILDREN_ORDER = new String[]{
        RESTEasyName.user.name(),
        RESTEasyName.password.name(),
        RESTEasyName.realm.name(),
        RESTEasyName.host.name(),
        RESTEasyName.port.name()
    };

    private RESTEasyNameValueModel _user;
    private RESTEasyNameValueModel _password;
    private RESTEasyNameValueModel _realm;
    private RESTEasyNameValueModel _host;
    private RESTEasyNameValueModel _port;

    /**
     * Creates a new BasicAuthModel.
     * @param namespace namespace
     */
    public V1BasicAuthModel(String namespace) {
        super(namespace, RESTEasyName.basic.name());
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new BasicAuthModel.
     * @param namespace namespace
     * @param name the name of teh model
     */
    public V1BasicAuthModel(String namespace, String name) {
        super(namespace, name);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new BasicAuthModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1BasicAuthModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        if (_user == null) {
            _user = getNameValue(RESTEasyName.user);
        }
        return _user != null ? _user.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel setUser(String user) {
        _user = setNameValue(_user, RESTEasyName.user, user);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        if (_password == null) {
            _password = getNameValue(RESTEasyName.password);
        }
        return _password != null ? _password.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel setPassword(String password) {
        _password = setNameValue(_password, RESTEasyName.password, password);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getRealm() {
        if (_realm == null) {
            _realm = getNameValue(RESTEasyName.realm);
        }
        return _realm != null ? _realm.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel setRealm(String realm) {
        _realm = setNameValue(_realm, RESTEasyName.realm, realm);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        if (_host == null) {
            _host = getNameValue(RESTEasyName.host);
        }
        return _host != null ? _host.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel setHost(String host) {
        _host = setNameValue(_host, RESTEasyName.host, host);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getPort() {
        if (_port == null) {
            _port = getNameValue(RESTEasyName.port);
        }
        return _port != null ? _port.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel setPort(String port) {
        _port = setNameValue(_port, RESTEasyName.port, port);
        return this;
    }

    protected RESTEasyNameValueModel getNameValue(RESTEasyName name) {
        return (RESTEasyNameValueModel)getFirstChildModel(name.name());
    }

    protected RESTEasyNameValueModel setNameValue(RESTEasyNameValueModel model, RESTEasyName name, String value) {
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
