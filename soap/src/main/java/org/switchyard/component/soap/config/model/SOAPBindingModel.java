/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.soap.config.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.PortName;
import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * A model that holds the SOAP gateway configuration.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPBindingModel extends V1BindingModel {

    /**
     *  Prefix for SOAP Gateway Configuration.
     */
    public static final String SOAP = "soap";
    
    /**
     * Default namespace for SOAP Gateway Configuration.
     */
    public static final String DEFAULT_NAMESPACE = 
        "urn:switchyard-component-soap:config:1.0";
    
    /**
     * Config property names.
     */
    private static final String WSDL = "wsdl";
    private static final String PORT = "wsdlPort";
    private static final String CONTEXT_PATH = "contextPath";
    private static final String COMPOSER = "composer";
    private static final String DECOMPOSER = "decomposer";
    private static final String SOCKET_ADDRESS = "socketAddr";

    private PortName _port;
    private String _wsdl;
    private QName _serviceName;
    private SocketAddr _socketAddr;
    private String _contextPath;
    private String _composer;
    private String _decomposer;
    private Boolean _publishAsWS = false;

    private Configuration _environment = Configurations.emptyConfig();
    /**
     * Constructor.
     */
    public SOAPBindingModel() {
        super(SOAP, DEFAULT_NAMESPACE);
        setModelChildrenOrder(WSDL, PORT, SOCKET_ADDRESS, COMPOSER, DECOMPOSER);
    }

    /**
     * Create a SOAPBindingModel using configuration and descriptor.
     * 
     * @param config the SOAPGateway configuration
     * @param desc the SOAPGateway descriptor
     */
    public SOAPBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Returns the WebService port.
     * 
     * @return the port
     */
    public PortName getPort() {
        if (_port == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(PORT);
            if (childConfig != null) {
                _port = new PortName(childConfig.getValue());
            } else {
                _port = new PortName();
            }
        }
        return _port;
    }

    /**
     * Sets the WebService port.
     * 
     * @param port the port to set
     */
    public void setPort(PortName port) {
        this._port = port;
    }

    /**
     * Returns the WebService WSDL.
     * 
     * @return the wsdl
     */
    public String getWsdl() {
        if (_wsdl == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(WSDL);
            if (childConfig != null) {
                _wsdl = childConfig.getValue();
            }
        }
        return _wsdl;
    }

    /**
     * Sets the WebService WSDL.
     * 
     * @param wsdl the wsdl to set
     */
    public void setWsdl(String wsdl) {
        this._wsdl = wsdl;
        Configuration childConfig = getModelConfiguration().getFirstChild(WSDL);
        if (childConfig == null) {
            ValueModel portConfig = new ValueModel(WSDL);
            portConfig.setValue(wsdl);
            setChildModel(portConfig);
        } else {
            childConfig.setValue(wsdl);
        }
    }

    /**
     * Returns the WebService Service name.
     * 
     * @return the serviceName
     */
    public QName getServiceName() {
        if (_serviceName == null) {
            _serviceName = getService().getQName();
        }
        return _serviceName;
    }

    /**
     * Sets the WebService Service name.
     * 
     * @param serviceName the serviceName to set
     */
    public void setServiceName(QName serviceName) {
        this._serviceName = serviceName;
    }

    /**
     * Returns the IP Socket Address where the WebService will be hosted.
     * 
     * This is applicable only if publishAsWS is true. 
     * 
     * @return the IP Socket Address
     */
    public SocketAddr getSocketAddr() {
        if (_socketAddr == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(SOCKET_ADDRESS);
            if (childConfig == null) {
                Configuration hostConfig = _environment.getFirstChild(SOCKET_ADDRESS);
                if (hostConfig != null && hostConfig.getValue() != null) {
                    _socketAddr = new SocketAddr(hostConfig.getValue());
                } else {
                    _socketAddr = new SocketAddr();
                }
            } else {
                _socketAddr = new SocketAddr(childConfig.getValue());
            }
        }
        return _socketAddr;
    }

    /**
     * Sets the IP Socket Address where the WebService will be hosted.
     * 
     * This is applicable only if publishAsWS is true.
     * 
     * @param socketAddr the IP Socket Address to set
     */
    public void setSocketAddr(SocketAddr socketAddr) {
        this._socketAddr = socketAddr;
    }

    /**
     * Gets the extra context path of the WebService.
     * 
     * This is applicable only if publishAsWS is true.
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
     * Sets the extra context path of the WebService.
     * 
     * This is applicable only if publishAsWS is true.
     * 
     * @param contextPath the contextPath to set
     */
    public void setContextPath(String contextPath) {
        this._contextPath = contextPath;
    }

    /**
     * Gets the MessageComposer class name.
     * 
     * @return the composer
     */
    public String getComposer() {
        if (_composer == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(COMPOSER);
            if (childConfig != null) {
                _composer = childConfig.getValue();
            }
        }
        return _composer;
    }

    /**
     * Gets the MessageComposer's mappedVariableNames.
     * @return the mappedVariableNames
     */
    public Set<QName> getComposerMappedVariableNames() {
        return getMappedVariableNames(COMPOSER);
    }

    /**
     * Sets the MessageComposer class name.
     * 
     * @param composer the composer to set
     */
    public void setComposer(String composer) {
        this._composer = composer;
    }

    /**
     * Gets the MessageDecomposer class name.
     * 
     * @return the decomposer
     */
    public String getDecomposer() {
        if (_decomposer == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild(DECOMPOSER);
            if (childConfig != null) {
                _decomposer = childConfig.getValue();
            }
        }
        return _decomposer;
    }

    /**
     * Gets the MessageDecomposer's mappedVariableNames.
     * @return the mappedVariableNames
     */
    public Set<QName> getDecomposerMappedVariableNames() {
        return getMappedVariableNames(DECOMPOSER);
    }

    /**
     * Sets the MessageDecomposer class name.
     * 
     * @param decomposer the decomposer to set
     */
    public void setDecomposer(String decomposer) {
        this._decomposer = decomposer;
    }

    /**
     * Gets the specified Configuration's mappedVariableNames.
     * @param childConfigName "composer" or "decomposer"
     * @return the mappedVariableNames
     */
    private Set<QName> getMappedVariableNames(String childConfigName) {
        Configuration childConfig = getModelConfiguration().getFirstChild(childConfigName);
        if (childConfig != null) {
            String names = Strings.trimToNull(childConfig.getAttribute("mappedVariableNames"));
            if (names != null) {
                String namespace = Strings.trimToNull(childConfig.getAttribute("mappedVariableNamespace"));
                Set<QName> qnames = new LinkedHashSet<QName>();
                for (String name : Strings.uniqueSplitTrimToNull(names, ",;| ")) {
                    QName qname;
                    if (namespace != null) {
                        qname = XMLHelper.createQName(namespace, name);
                    } else {
                        qname = XMLHelper.createQName(name);
                    }
                    qnames.add(qname);
                }
                return qnames;
            }
        }
        return Collections.emptySet();
    }

    /**
     * Sets if the SOAPGateway needs to publish a WebService using this configuration.
     * 
     * @param publishAsWS the publishAsWS to set
     */
    public void setPublishAsWS(Boolean publishAsWS) {
        this._publishAsWS = publishAsWS;
    }

    /**
     * Gets if the SOAPGateway needs to publish a WebService using this configuration.
     * 
     * @return the publishAsWS
     */
    public Boolean getPublishAsWS() {
        return _publishAsWS;
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
        super(new QName(SOAPBindingModel.DEFAULT_NAMESPACE, name));
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
