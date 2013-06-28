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
package org.switchyard.component.common.knowledge.service;

import javax.xml.namespace.QName;

import org.kie.api.runtime.Channel;

/**
 * SwitchYardServiceChannel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceChannel implements Channel {

    /** service. */
    public static final String SERVICE = "service";

    private String _name;
    private SwitchYardServiceInvoker _invoker;
    private QName _serviceName;
    private String _operationName;
    private QName _componentName;

    /**
     * Constructs a new SwitchYardServiceChannel with the name "service".
     */
    public SwitchYardServiceChannel() {
        setName(SERVICE);
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the invoker.
     * @return the invoker
     */
    public SwitchYardServiceInvoker getInvoker() {
        return _invoker;
    }

    /**
     * Sets the invoker.
     * @param invoker the invoker
     */
    public void setInvoker(SwitchYardServiceInvoker invoker) {
        _invoker = invoker;
    }
    
    /**
     * Set the service component name associated with this invoker.
     * @param componentName service component name
     */
    public void setComponentName(QName componentName) {
        _componentName = componentName;
    }
    
    /**
     * Get the service component name associated with this invoker.
     * @return service component name
     */
    public QName getComponentName() {
        return _componentName;
    }

    /**
     * Gets the service name.
     * @return the service name
     */
    public QName getServiceName() {
        return _serviceName;
    }

    /**
     * Sets the service name.
     * @param serviceName the service name
     */
    public void setServiceName(QName serviceName) {
        _serviceName = serviceName;
    }

    /**
     * Gets the operation name.
     * @return the operation name
     */
    public String getOperationName() {
        return _operationName;
    }

    /**
     * Sets the operation name.
     * @param operationName the operation name
     */
    public void setOperationName(String operationName) {
        _operationName = operationName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Object object) {
        getInvoker().invoke(new SwitchYardServiceRequest(getServiceName(), getOperationName(), object));
    }

}
