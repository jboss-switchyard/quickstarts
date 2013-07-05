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
