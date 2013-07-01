/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.component.resteasy.config.model.v1;

import javax.xml.namespace.QName;

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
        RESTEasyName.proxy.name()
    };

    private QName _serviceName;
    private Configuration _environment;

    private RESTEasyNameValueModel _address;
    private RESTEasyNameValueModel _interfaces;
    private RESTEasyNameValueModel _contextPath;
    private ProxyModel _proxyConfig;

    /**
     * Constructor.
     */
    public V1RESTEasyBindingModel() {
        super(RESTEASY, DEFAULT_NAMESPACE);
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
        return getInterfaces().split(",");
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
                model = new V1RESTEasyNameValueModel(name);
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
