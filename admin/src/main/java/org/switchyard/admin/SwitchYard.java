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

package org.switchyard.admin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * Top-level admin interface for SwitchYard runtime.
 */
public interface SwitchYard {

    /**
     * The version of the SwitchYard runtime.
     * 
     * @return SwitchYard version
     */
    String getVersion();

    /**
     * List of applications current deployed in SwitchYard runtime.
     * 
     * @return list of SwitchYard applications
     */
    List<Application> getApplications();

    /**
     * List of implementation and gateway components currently installed in
     * SwitchYard runtime.
     * 
     * @return list of SwitchYard components
     */
    List<Component> getComponents();

    /**
     * List of services currently registered in the SwitchYard runtime.
     * 
     * @return list of SwitchYard services
     */
    List<Service> getServices();

    /**
     * Find a component with the specified name.
     * 
     * @param name
     *            the name of the component.
     * @return the component with the specified name; may be null if a component
     *         with the specified name is not registered with the system.
     */
    Component getComponent(String name);

    /**
     * Find an application with the specified name.
     * 
     * @param name
     *            the name of the application.
     * @return the application with the specified name; may be null if an
     *         application with the specified name is not deployed on the
     *         system.
     */
    Application getApplication(QName name);
    
    /**
     * @return the socket binding names configured for SwitchYard.
     */
    Set<String> getSocketBindingNames();
    
    /**
     * @return the SwitchYard system configuration properties.
     */
    Map<String,String> getProperties();

}
