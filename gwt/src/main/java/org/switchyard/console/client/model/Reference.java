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
package org.switchyard.console.client.model;

import java.util.List;

/**
 * Reference
 * 
 * Represents a SwitchYard reference.
 */
public interface Reference extends HasQName {

    /**
     * @return the interface name
     */
    public String getInterface();

    /**
     * @param interfaceName the interface name
     */
    public void setInterface(String interfaceName);

    /**
     * @return the gateways
     */
    List<Binding> getGateways();

    /**
     * @param gateways the gateways
     */
    public void setGateways(List<Binding> gateways);

    /**
     * @return the application name
     */
    public String getApplication();

    /**
     * @param application the application name
     */
    public void setApplication(String application);

}
