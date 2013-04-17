/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.resteasy.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * A model that holds the RESTEasy gateway configuration.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyBindingModel extends V1BindingModel {

    /**
     *  Prefix for RESTEasy Gateway Configuration.
     */
    public static final String RESTEASY = "rest";

    /**
     * Default namespace for RESTEasy Gateway Configuration.
     */
    public static final String DEFAULT_NAMESPACE = 
        "urn:switchyard-component-resteasy:config:1.0";

    /**
     * Config property names.
     */
    private static final String ADDRESS = "address";
    private static final String CONTEXT_PATH = "contextPath";
    private static final String RESOURCE_INTERFACES = "interfaces";

    private QName _serviceName;
    private String _address;
    private String _resourceInterfaces;
    private String _contextPath;

    private Configuration _environment = Configurations.newConfiguration();

    /**
     * Constructor.
     */
    public RESTEasyBindingModel() {
        super(RESTEASY, DEFAULT_NAMESPACE);
        setModelChildrenOrder(RESOURCE_INTERFACES, ADDRESS, CONTEXT_PATH);
    }

    /**
     * Create a RESTEasyBindingModel using configuration and descriptor.
     * 
     * @param config the RESTEasyGateway configuration
     * @param desc the RESTEasyGateway descriptor
     */
    public RESTEasyBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Returns the RESTEasy Service name.
     * 
     * @return the serviceName
     */
    public QName getServiceName() {
        if (_serviceName == null) {
            _serviceName = isServiceBinding() ? getService().getQName() : getReference().getQName();
        }
        return _serviceName;
    }

    /**
     * Sets the RESTEasy Service name.
     * 
     * @param serviceName the serviceName to set
     */
    public void setServiceName(QName serviceName) {
        _serviceName = serviceName;
    }

    /**
     * Gets the REST endpoint address invoked by the Reference binding.
     * 
     * @return the endpoint address
     */
    public String getAddress() {
        if (_address == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(ADDRESS);
            if (childConfig != null) {
                _address = childConfig.getValue();
            }
        }
        return _address;
    }

    /**
     * Sets the REST endpoint address invoked by the Reference binding.
     * 
     * @param address the endpoint address
     */
    public void setAddress(String address) {
        _address = address;
        Configuration childConfig = getModelConfiguration().getFirstChild(ADDRESS);
        if (childConfig == null) {
            ValueModel addressConfig = new ValueModel(ADDRESS);
            addressConfig.setValue(address);
            setChildModel(addressConfig);
        } else {
            childConfig.setValue(address);
        }
    }

    /**
     * Gets the REST interfaces exposed by this Service binding.
     * 
     * @return the comma seperated list of RESTEasy interfaces
     */
    public String getInterfaces() {
        if (_resourceInterfaces == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(RESOURCE_INTERFACES);
            _resourceInterfaces = childConfig.getValue();
        }
        return _resourceInterfaces;
    }

    /**
     * Gets the REST interfaces exposed by this Service binding.
     * 
     * @return the list of RESTEasy interfaces as an array
     */
    public String[] getInterfacesAsArray() {
        if (_resourceInterfaces == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(RESOURCE_INTERFACES);
            _resourceInterfaces = childConfig.getValue();
        }
        return _resourceInterfaces.split(",");
    }

    /**
     * Sets the REST interfaces exposed by this Service binding.
     * 
     * @param interfaces a comma separated list of JAVA class names
     */
    public void setInterfaces(String interfaces) {
        _resourceInterfaces = interfaces;
        Configuration childConfig = getModelConfiguration().getFirstChild(RESOURCE_INTERFACES);
        if (childConfig == null) {
            ValueModel intfConfig = new ValueModel(RESOURCE_INTERFACES);
            intfConfig.setValue(interfaces);
            setChildModel(intfConfig);
        } else {
            childConfig.setValue(interfaces);
        }
    }

    /**
     * Gets the context path of the Resource interfaces.
     * 
     * This is applicable only if getPublish is true.
     * 
     * @return the contextPath
     */
    public String getContextPath() {
        if (_contextPath == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(CONTEXT_PATH);
            if (childConfig == null) {
                Configuration contextConfig = _environment.getFirstChild(CONTEXT_PATH);
                if (contextConfig != null && contextConfig.getValue() != null) {
                    _contextPath = contextConfig.getValue();
                }
            } else {
                _contextPath = childConfig.getValue();
            }
        }
        return _contextPath;
    }

    /**
     * Sets the context path of the Resource interfaces.
     * 
     * This is applicable only if getPublish() is true.
     * 
     * @param contextPath the contextPath to set
     */
    public void setContextPath(String contextPath) {
        this._contextPath = contextPath;
        Configuration childConfig = getModelConfiguration().getFirstChild(CONTEXT_PATH);
        if (childConfig == null) {
            ValueModel pathConfig = new ValueModel(CONTEXT_PATH);
            pathConfig.setValue(contextPath);
            setChildModel(pathConfig);
        } else {
            childConfig.setValue(contextPath);
        }
    }

    /**
     * Sets the global configuration.
     * 
     * @param config the environment/global config
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }
}

class ValueModel extends BaseModel {
    
    public ValueModel(String name) {
        super(new QName(RESTEasyBindingModel.DEFAULT_NAMESPACE, name));
    }
    
    public ValueModel(Configuration config) {
        super(config);
    }
    
    public String getValue() {
        return super.getModelValue();
    }
    
    public void setValue(String value) {
        super.setModelValue(value);
    }
}
