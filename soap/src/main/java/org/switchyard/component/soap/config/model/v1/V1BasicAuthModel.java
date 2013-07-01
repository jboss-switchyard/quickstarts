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

import static org.switchyard.component.soap.config.model.SOAPBindingModel.DEFAULT_NAMESPACE;

import org.switchyard.component.soap.config.model.BasicAuthModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel.SOAPName;
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
        SOAPName.user.name(),
        SOAPName.password.name()
    };

    private SOAPNameValueModel _user;
    private SOAPNameValueModel _password;

    /**
     * Creates a new BasicAuthModel.
     */
    public V1BasicAuthModel() {
        super(SOAPName.basic.name(), DEFAULT_NAMESPACE);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new BasicAuthModel.
     * @param name the name of teh model
     */
    public V1BasicAuthModel(String name) {
        super(name, DEFAULT_NAMESPACE);
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
            _user = getNameValue(SOAPName.user);
        }
        return _user != null ? _user.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel setUser(String user) {
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
    public BasicAuthModel setPassword(String password) {
        _password = setNameValue(_password, SOAPName.password, password);
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
                model = new V1SOAPNameValueModel(name);
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
