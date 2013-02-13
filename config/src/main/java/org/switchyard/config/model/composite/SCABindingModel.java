/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.config.model.composite;

import javax.xml.namespace.QName;

import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * SCABindingModel represents the standard binding.sca binding in SCA.  SwitchYard supports a 
 * set of extensions which are represented as attributes on the binding.sca element which are exposed
 * via this config model.
 */
public interface SCABindingModel extends BindingModel {
    
    /** The "sca" name. */
    public static final String SCA = "sca";
    
    /** The "target" name. */
    public static final QName TARGET = new QName(SwitchYardModel.DEFAULT_NAMESPACE, "target");
    
    /** The "target" name. */
    public static final QName TARGET_NAMESPACE = new QName(SwitchYardModel.DEFAULT_NAMESPACE, "targetNamespace");
    
    /** The "loadBalance" name. */
    public static final QName LOAD_BALANCE = new QName(SwitchYardModel.DEFAULT_NAMESPACE, "loadBalance");
    
    /** The "clustered" name. */
    public static final QName CLUSTERED = new QName(SwitchYardModel.DEFAULT_NAMESPACE, "clustered");

    /**
     * Indicates whether clustering is enabled.  
     * @return true if clustering is enabled, false otherwise
     */
    boolean isClustered();
    
    /**
     * Specifies whether clustering is enabled for the service binding.  Valid for service and 
     * reference bindings.
     * @param clustered true for enabled, false for disabled
     * @return this config model instance
     */
    SCABindingModel setClustered(boolean clustered);
    
    /**
     * Indicates whether load balancing is enabled.
     * @return true if load balancing is enabled, false otherwise
     */
    boolean isLoadBalanced();
    
    /**
     * Returns the load balance strategy used by the binding.
     * @return the load balance strategy in use
     */
    String getLoadBalance();
    
    /**
     * Specifies the strategy used for load balancing.  This attribute is only valid for reference
     * bindings.
     * @param loadBalance the load balance strategy used
     * @return this config model instance
     */
    SCABindingModel setLoadBalance(String loadBalance);
    
    /**
     * Indicates whether a target service has been specified for the binding.
     * @return true if a target service is specified, false otherwise
     */
    boolean hasTarget();
    
    /**
     * Returns the target service name used for the binding.
     * @return target service name
     */
    String getTarget();
    
    /**
     * Specifies the target service name used for this binding.  This is valid for reference 
     * bindings only and allows the service name to be overloaded in case the provider service
     * has a different name.
     * @param target target service name
     * @return this config model instance
     */
    SCABindingModel setTarget(String target);
    
    /**
     * Indicates whether a target namespace has been specified for the binding.
     * @return true if a target namespace is specified, false otherwise
     */
    boolean hasTargetNamespace();
    
    /**
     * Returns the target namespace used for the binding.
     * @return target namespace
     */
    String getTargetNamespace();
    
    /**
     * Specifies the target namespace used for this binding.  This is valid for reference 
     * bindings only and allows the service namespace to be overloaded in case the provider service
     * has a different namespace.
     * @param namespace target service namespace
     * @return this config model instance
     */
    SCABindingModel setTargetNamespace(String namespace);
}
