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

import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "component/service" model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentServiceModel extends NamedModel {

    /** The "service"  name. */
    public static final String SERVICE = CompositeServiceModel.SERVICE;

    /** The "security" name. */
    public static final QName SECURITY = new QName(SwitchYardModel.DEFAULT_NAMESPACE, "security");

    /**
     * Gets the parent component model.
     * @return the parent component model
     */
    public ComponentModel getComponent();

    /**
     * Gets the child component service interface model.
     * @return the child component service interface model
     */
    public InterfaceModel getInterface();

    /**
     * Sets the child component service interface model.
     * @param interfaze child component service interface model
     * @return this ComponentServiceModel (useful for chaining)
     */
    public ComponentServiceModel setInterface(InterfaceModel interfaze);
    
    /**
     * Gets the name of the security section to respect.
     * @return the name of the security section to respect
     */
    public String getSecurity();
    
    /**
     * Sets the name of the security section to respect.
     * @param security the name of the security section to respect
     * @return this ComponentServiceModel (useful for chaining)
     */
    public ComponentServiceModel setSecurity(String security);
    
    /**
     * Adds the specified policy identifier to the set of required policy 
     * intents for the service.
     * @param policyName name of the required policy
     */
    public void addPolicyRequirement(String policyName);
    
    /**
     * Returns the set of policy intents required for this service.
     * @return set of policy intents required for the service.
     */
    public Set<String> getPolicyRequirements();
    
    /**
     * Indicates whether a given policy is required for this service.
     * @param policyName name of the policy to check
     * @return true if the policy is required, false otherwise
     */
    public boolean hasPolicyRequirement(String policyName);

}
