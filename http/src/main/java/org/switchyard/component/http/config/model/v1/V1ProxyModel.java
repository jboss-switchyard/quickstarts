/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.http.config.model.v1;

import static org.switchyard.component.http.config.model.HttpBindingModel.DEFAULT_NAMESPACE;

import org.switchyard.component.http.config.model.ProxyModel;
import org.switchyard.component.http.config.model.HttpNameValueModel;
import org.switchyard.component.http.config.model.HttpNameValueModel.HttpName;
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
        HttpName.host.name(),
        HttpName.port.name(),
        HttpName.user.name(),
        HttpName.password.name()
    };

    private HttpNameValueModel _host;
    private HttpNameValueModel _port;
    private HttpNameValueModel _user;
    private HttpNameValueModel _password;

    /**
     * Creates a new ProxyModel.
     */
    public V1ProxyModel() {
        super(HttpName.proxy.name(), DEFAULT_NAMESPACE);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new ProxyModel.
     * @param name the name of teh model
     */
    public V1ProxyModel(String name) {
        super(name, DEFAULT_NAMESPACE);
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
    public String getHost() {
        if (_host == null) {
            _host = getNameValue(HttpName.host);
        }
        return _host != null ? _host.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setHost(String host) {
        _host = setNameValue(_host, HttpName.host, host);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getPort() {
        if (_port == null) {
            _port = getNameValue(HttpName.port);
        }
        return _port != null ? _port.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        if (_user == null) {
            _user = getNameValue(HttpName.user);
        }
        return _user != null ? _user.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setUser(String user) {
        _user = setNameValue(_user, HttpName.user, user);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        if (_password == null) {
            _password = getNameValue(HttpName.password);
        }
        return _password != null ? _password.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setPassword(String password) {
        _password = setNameValue(_password, HttpName.password, password);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyModel setPort(String port) {
        _port = setNameValue(_port, HttpName.port, port);
        return this;
    }

    protected HttpNameValueModel getNameValue(HttpName name) {
        return (HttpNameValueModel)getFirstChildModel(name.name());
    }

    protected HttpNameValueModel setNameValue(HttpNameValueModel model, HttpName name, String value) {
        if (value != null) {
            if (model == null) {
                model = getNameValue(name);
            }
            if (model == null) {
                model = new V1HttpNameValueModel(name);
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
