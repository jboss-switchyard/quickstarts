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

import static org.switchyard.component.soap.config.model.EndpointConfigModel.ENDPOINT_CONFIG;
import static org.switchyard.component.soap.config.model.InterceptorsModel.IN_INTERCEPTORS;
import static org.switchyard.component.soap.config.model.InterceptorsModel.OUT_INTERCEPTORS;

import javax.xml.namespace.QName;

import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.SOAPMessages;
import org.switchyard.component.soap.PortName;
import org.switchyard.component.soap.config.model.EndpointConfigModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.component.soap.config.model.BasicAuthModel;
import org.switchyard.component.soap.config.model.MtomModel;
import org.switchyard.component.soap.config.model.NtlmAuthModel;
import org.switchyard.component.soap.config.model.ProxyModel;
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
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1SOAPBindingModel extends V1BindingModel implements SOAPBindingModel {

    private static final String[] MODEL_CHILDREN_ORDER = new String[]{
        SOAPName.wsdl.name(),
        SOAPName.wsdlPort.name(),
        SOAPName.socketAddr.name(),
        SOAPName.contextPath.name(),
        SOAPName.endpointAddress.name(),
        SOAPName.timeout.name(),
        SOAPName.basic.name(),
        SOAPName.ntlm.name(),
        SOAPName.proxy.name(),
        ENDPOINT_CONFIG,
        SOAPName.mtom.name(),
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
    private SOAPNameValueModel _timeout;
    private BasicAuthModel _basicAuth;
    private NtlmAuthModel _ntlmAuth;
    private ProxyModel _proxyConfig;
    private MtomModel _mtomConfig;
    private EndpointConfigModel _endpointConfig;
    private InterceptorsModel _inInterceptors;
    private InterceptorsModel _outInterceptors;

    /**
     * Creates a new SOAPBindingModel.
     * @param namespace namespace
     */
    public V1SOAPBindingModel(String namespace) {
        super(SOAP, namespace);
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
    public Integer getTimeout() {
        if (_timeout == null) {
            _timeout = getNameValue(SOAPName.timeout);
        }
        return _timeout != null ? Integer.valueOf(_timeout.getValue()) : null;
    }

    /**
     * {@inheritDoc}
     */
    public SOAPBindingModel setTimeout(Integer timeout) {
        _timeout = setNameValue(_timeout, SOAPName.timeout, String.valueOf(timeout));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BasicAuthModel getBasicAuthConfig() {
        if (_basicAuth == null) {
            _basicAuth = (BasicAuthModel)getFirstChildModel(SOAPName.basic.name());
        }
        return _basicAuth;
    }

    /**
     * {@inheritDoc}
     */
    public SOAPBindingModel setBasicAuthConfig(BasicAuthModel config) {
        setChildModel(config);
        _basicAuth = config;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public NtlmAuthModel getNtlmAuthConfig() {
        if (_ntlmAuth == null) {
            _ntlmAuth = (NtlmAuthModel)getFirstChildModel(SOAPName.ntlm.name());
        }
        return _ntlmAuth;
    }

    /**
     * {@inheritDoc}
     */
    public SOAPBindingModel setNtlmAuthConfig(NtlmAuthModel config) {
        setChildModel(config);
        _ntlmAuth = config;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyModel getProxyConfig() {
        if (_proxyConfig == null) {
            _proxyConfig = (ProxyModel)getFirstChildModel(SOAPName.proxy.name());
        }
        return _proxyConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setProxyConfig(ProxyModel proxyConfig) {
        setChildModel(proxyConfig);
        _proxyConfig = proxyConfig;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MtomModel getMtomConfig() {
        if (_mtomConfig == null) {
            _mtomConfig = (MtomModel)getFirstChildModel(SOAPName.mtom.name());
        }
        return _mtomConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingModel setMtomConfig(MtomModel mtomConfig) {
        setChildModel(mtomConfig);
        _mtomConfig = mtomConfig;
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
                throw SOAPMessages.MESSAGES.not(IN_INTERCEPTORS);
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
                throw SOAPMessages.MESSAGES.not(OUT_INTERCEPTORS);
            }
        }
        setChildModel(outInterceptors);
        _outInterceptors = outInterceptors;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isUnwrapped() {
        return getSOAPMessageComposer() != null && getSOAPMessageComposer().isUnwrapped();
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
