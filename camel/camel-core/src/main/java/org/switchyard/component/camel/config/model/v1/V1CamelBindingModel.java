/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.v1;

import java.net.URI;

import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.camel.config.model.ConfigURI;
import org.switchyard.component.camel.config.model.ConfigURIFactory;
import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A generic binding definintion for Apache Camel components.
 * 
 * Example of a binding implememtation:
 * <pre>
 * {@code
 *  <sca:service name="SimpleCamelService">
 *     <camel:binding.camel configURI="direct://input">
 *        <camel:operationSelector operationName="print"/>
 *     </camel:binding.camel>
 *  </sca:service>
 * }
 * </pre>
 * 
 * <ul>
 * <li> The 'configURI' attribute can be used to specify a Camel component uri.</li>
 * </ul>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelBindingModel extends V1BaseCamelBindingModel {
    
    /**
     * The binding type (binding.camel).
     */
    public static final String CAMEL = "camel";
    
    /**
     * This is the name of the uri attribute. 
     */
    public static final String CONFIG_URI = "configURI";
    
    /**
     * This is the name of the transacted attribute. 
     */
    public static final String TRANSACTED = "transacted";

    /**
     * The name of the transacedRef attribute.
     */
    public static final String TRANSACTED_REF = "transactedRef";

    private static final String SOCKET_ADDRESS = "socketAddr";
    private static final String CONTEXT_PATH = "contextPath";
    
    private ConfigURI _configURI;
    
    /**
     * Create a new CamelBindingModel.
     */
    public V1CamelBindingModel() {
        super(CAMEL);
    }
    
    /**
     * Create a CamelBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    /**
     * Gets the binding uri attribute from the underlying model.
     * 
     * @return URI The binding uri attribute.
     */
    public ConfigURI getConfigURI() {
        if (_configURI == null) {
            _configURI = ConfigURIFactory.newConfigURI(getModelAttribute(CONFIG_URI), getSocketAddr(), getContextPath());
        }
        return _configURI;
    }

    /**
     * Sets the "uri" element on the underlying model.
     * 
     * @param uri The Camel config URI
     * @return {@link CamelBindingModel} to support method chaining.
     */
    public V1CamelBindingModel setConfigURI(ConfigURI uri) {
        if (_configURI == null) {
            setModelAttribute(CONFIG_URI, uri.toString());
            _configURI = uri;
        }
        return this;
    }

    /**
     * Sets the "uri" element on the underlying model.
     * 
     * @param uri The Camel Component URI
     * @return {@link CamelBindingModel} to support method chaining.
     */
    public V1CamelBindingModel setConfigURI(URI uri) {
        if (_configURI == null) {
            setModelAttribute(CONFIG_URI, uri.toString());
            _configURI = ConfigURIFactory.newConfigURI(uri.toString(), getSocketAddr(), getContextPath());
        }
        return this;
    }

    private SocketAddr getSocketAddr() {
        Configuration hostConfig = getEnvironment().getFirstChild(SOCKET_ADDRESS);
        SocketAddr socketAddr = null;
        if (hostConfig != null && hostConfig.getValue() != null) {
            socketAddr = new SocketAddr(hostConfig.getValue());
        } else {
            socketAddr = new SocketAddr();
        }
        return socketAddr;
    }

    private String getContextPath() {
        Configuration hostConfig = getEnvironment().getFirstChild(CONTEXT_PATH);
        String contextPath = "";
        if (hostConfig != null && hostConfig.getValue() != null) {
            contextPath = "/" + hostConfig.getValue();
        }
        return contextPath;
    }
    
    @Override
    public URI getComponentURI() {
        return getConfigURI().getURI();
    }

    @Override
    public V1CamelBindingModel setOperationSelector(OperationSelector operationSelector) {
        super.setOperationSelector(operationSelector);
        return this;
    }

}
