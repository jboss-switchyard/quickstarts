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

import javax.xml.namespace.QName;

import org.switchyard.component.soap.PortName;
import org.switchyard.config.Configuration;
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

    private static final int DEFAULT_PORT = 8080;

    private PortName _port;
    private String _wsdl;
    private QName _serviceName;
    private String _serverHost;
    private int _serverPort = -1;
    private String _contextPath;
    private String _composer;
    private String _decomposer;
    private Boolean _publishAsWS = false;
    /**
     * Constructor.
     */
    public SOAPBindingModel() {
        super(SOAP);
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
            Configuration childConfig = getModelConfiguration().getFirstChild("port");
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
            Configuration childConfig = getModelConfiguration().getFirstChild("wsdl");
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
     * Returns the host where the WebService will be hosted.
     * 
     * This is applicable only if publishAsWS is true. 
     * 
     * @return the serverHost
     */
    public String getServerHost() {
        if (_serverHost == null) {
            Configuration childConfig = getModelConfiguration().getFirstChild("serverHost");
            if (childConfig == null) {
                _serverHost = "localhost";
            } else {
                _serverHost = childConfig.getValue();
            }
        }
        return _serverHost;
    }

    /**
     * Sets the host where the WebService will be hosted.
     * 
     * This is applicable only if publishAsWS is true.
     * 
     * @param serverhost the serverHost to set
     */
    public void setServerHost(String serverhost) {
        this._serverHost = serverhost;
    }

    /**
     * Returns the server port where the WebService will be hosted.
     * 
     * This is applicable only if publishAsWS is true.
     * 
     * @return the serverPort
     */
    public int getServerPort() {
        if (_serverPort == -1) {
            Configuration childConfig = getModelConfiguration().getFirstChild("serverPort");
            if (childConfig == null) {
                _serverPort = DEFAULT_PORT;
            } else {
                _serverPort = Integer.parseInt(childConfig.getValue());
            }
        }
        return _serverPort;
    }

    /**
     * Sets the server port where the WebService will be hosted.
     * 
     * This is applicable only if publishAsWS is true.
     * 
     * @param serverPort the serverPort to set
     */
    public void setServerPort(int serverPort) {
        this._serverPort = serverPort;
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
            Configuration childConfig = getModelConfiguration().getFirstChild("contextPath");
            if (childConfig != null) {
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
            Configuration childConfig = getModelConfiguration().getFirstChild("composer");
            if (childConfig != null) {
                _composer = childConfig.getValue();
            }
        }
        return _composer;
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
            Configuration childConfig = getModelConfiguration().getFirstChild("decomposer");
            if (childConfig != null) {
                _decomposer = childConfig.getValue();
            }
        }
        return _decomposer;
    }

    /**
     * Sets the MessageComposer class name.
     * 
     * @param decomposer the decomposer to set
     */
    public void setDecomposer(String decomposer) {
        this._decomposer = decomposer;
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
}
