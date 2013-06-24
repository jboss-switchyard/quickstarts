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

package org.switchyard;

import java.util.List;

import org.switchyard.metadata.Registrant;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.policy.Policy;

/**
 * Contains runtime details on services and service references registered
 * in SwitchYard.  Instances of ServiceMetadata can be created and updated
 * using the ServiceMetadataBuilder class.
 */
public interface ServiceMetadata {

    /**
     * Gets the security.
     * @return the security
     */
    ServiceSecurity getSecurity();
    
    /**
     * Returns a list of required policies for this service reference.
     * @return list of required policy
     */
    List<Policy> getRequiredPolicies();

    /**
     * Returns a list of policies provided by this service reference.
     * @return list of provided policy
     */
    List<Policy> getProvidedPolicies();
    
    /**
     * Return the consumer metadata associated with this service.
     * @return consumer metadata
     */
    Registrant getRegistrant();
    
    /**
     * Return the throttling configuration associated with a service reference.  Throttling
     * config only applies to consumers via service references.
     * @return throttling config
     */
    Throttling getThrottling();
}
