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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.config.model.EndpointConfigModel.ENDPOINT_CONFIG;
import static org.switchyard.component.soap.config.model.InterceptorsModel.IN_INTERCEPTORS;
import static org.switchyard.component.soap.config.model.InterceptorsModel.OUT_INTERCEPTORS;

import javax.xml.namespace.QName;

import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.PortName;
import org.switchyard.component.soap.config.model.EndpointConfigModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPContextMapperModel;
import org.switchyard.component.soap.config.model.SOAPMessageComposerModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel.SOAPName;
import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * The 1st version SOAPBindingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1SOAPBindingModel extends V1BindingModel implements SOAPBindingModel {

    private static final String[] MODEL_CHILDREN_ORDER = new String[]{
        SOAPName.wsdl.name(),
        SOAPName.wsdlPort.name(),
        SOAPName.socketAddr.name(),
        SOAPName.contextPath.name(),
        SOAPName.endpointAddress.name(),
        ENDPOINT_CONFIG,
        IN_INTERCEPTORS,
        OUT_INTERCEPTORS
    };

    private Configuration _environment;
    private QName _serviceName;

    private SOAPNameValueModel _wsdl;
    private PortName _port;
    private SocketAddr _socketAddr;
    private SOAPNameValueModel _contextPath;
    private SOAPNameValueModel _endpointAddress;
    private EndpointConfigModel _endpointConfig;
    private InterceptorsModel _inInterceptors;
    private InterceptorsModel _outInterceptors;

    /**
     * Creates a new SOAPBindingModel.
     */
    public V1SOAPBindingModel() {
        super(SOAP, DEFAULT_NAMESPACE);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * Creates a new SOAPBindingModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1SOAPBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(MODEL_CHILDREN_ORDER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPContextMapperModel getSOAPContextMapper() {
        return (SOAPContextMapperModel)getContextMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPMessageComposerModel getSOAPMessageComposer() {
        return (SOAPMessageComposerModel)getMessageComposer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public SOAPBindingModel setEnvironment(Configuration environment) {
        _environment = environment;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getServiceName() {
        if (_serviceName == null) {
            _serviceName = isServiceBinding() ? getService().getQName() : getReference().getQName();
        }
        return _serviceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setServiceName(QName serviceName) {
        _serviceName = serviceName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWsdl() {
        if (_wsdl == null) {
            _wsdl = getNameValue(SOAPName.wsdl);
        }
        return _wsdl != null ? _wsdl.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setWsdl(String wsdl) {
        _wsdl = setNameValue(_wsdl, SOAPName.wsdl, wsdl);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortName getPort() {
        if (_port == null) {
            SOAPNameValueModel wsdlPortModel = getNameValue(SOAPName.wsdlPort);
            if (wsdlPortModel != null) {
                _port = new PortName(wsdlPortModel.getValue());
            } else {
                _port = new PortName();
            }
        }
        return _port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setPort(PortName port) {
        _port = port;
        setNameValue(null, SOAPName.wsdlPort, port != null ? port.getName() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddr getSocketAddr() {
        if (_socketAddr == null) {
            SOAPNameValueModel socketAddrModel = getNameValue(SOAPName.socketAddr);
            String socketAddrValue = socketAddrModel != null ? socketAddrModel.getValue() : null;
            if (socketAddrValue == null) {
                Configuration socketAddrEnvConfig = getEnvironment().getFirstChild(SOAPName.socketAddr.name());
                socketAddrValue = socketAddrEnvConfig != null ? socketAddrEnvConfig.getValue() : null;
            }
            if (socketAddrValue != null) {
                _socketAddr = new SocketAddr(socketAddrValue);
            } else {
                _socketAddr = new SocketAddr();
            }
        }
        return _socketAddr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setSocketAddr(SocketAddr socketAddr) {
        _socketAddr = socketAddr;
        setNameValue(null, SOAPName.socketAddr, socketAddr != null ? socketAddr.toString() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContextPath() {
        if (_contextPath == null) {
            _contextPath = getNameValue(SOAPName.contextPath);
        }
        String contextPath = _contextPath != null ? _contextPath.getValue() : null;
        if (contextPath == null) {
            Configuration contextPathConfig = getEnvironment().getFirstChild(SOAPName.contextPath.name());
            contextPath = contextPathConfig != null ? contextPathConfig.getValue() : null;
        }
        return contextPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setContextPath(String contextPath) {
        _contextPath = setNameValue(_contextPath, SOAPName.contextPath, contextPath);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndpointAddress() {
        if (_endpointAddress == null) {
            _endpointAddress = getNameValue(SOAPName.endpointAddress);
        }
        return _endpointAddress != null ? _endpointAddress.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setEndpointAddress(String endpointAddress) {
        _endpointAddress = setNameValue(_endpointAddress, SOAPName.endpointAddress, endpointAddress);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EndpointConfigModel getEndpointConfig() {
        if (_endpointConfig == null) {
            _endpointConfig = (EndpointConfigModel)getFirstChildModel(ENDPOINT_CONFIG);
        }
        return _endpointConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setEndpointConfig(EndpointConfigModel endpointConfig) {
        setChildModel(endpointConfig);
        _endpointConfig = endpointConfig;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterceptorsModel getInInterceptors() {
        if (_inInterceptors == null) {
            _inInterceptors = (InterceptorsModel)getFirstChildModel(IN_INTERCEPTORS);
        }
        return _inInterceptors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setInInterceptors(InterceptorsModel inInterceptors) {
        if (inInterceptors != null) {
            if (!IN_INTERCEPTORS.equals(inInterceptors.getModelConfiguration().getName())) {
                throw new IllegalArgumentException("not " + IN_INTERCEPTORS);
            }
        }
        setChildModel(inInterceptors);
        _inInterceptors = inInterceptors;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterceptorsModel getOutInterceptors() {
        if (_outInterceptors == null) {
            _outInterceptors = (InterceptorsModel)getFirstChildModel(OUT_INTERCEPTORS);
        }
        return _outInterceptors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setOutInterceptors(InterceptorsModel outInterceptors) {
        if (outInterceptors != null) {
            if (!OUT_INTERCEPTORS.equals(outInterceptors.getModelConfiguration().getName())) {
                throw new IllegalArgumentException("not " + OUT_INTERCEPTORS);
            }
        }
        setChildModel(outInterceptors);
        _outInterceptors = outInterceptors;
        return this;
    }

    private SOAPNameValueModel getNameValue(SOAPName name) {
        return (SOAPNameValueModel)getFirstChildModel(name.name());
    }

    private SOAPNameValueModel setNameValue(SOAPNameValueModel model, SOAPName name, String value) {
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
