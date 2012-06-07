/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.config.model.domain;

import org.switchyard.config.model.Model;

/**
 * A Security Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface SecurityModel extends Model {

    /**
     * The security XML element.
     */
    public static final String SECURITY = "security";

    /**
     * Gets the parent domain model.
     * @return the parent domain model
     */
    public DomainModel getDomain();

    /**
     * Gets the CallbackHandler class.
     * @param loader the ClassLoader to use
     * @return the CallbackHandler class
     */
    public Class<?> getCallbackHandler(ClassLoader loader);

    /**
     * Sets the CallbackHandler class.
     * @param callbackHandler the CallbackHandler class
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setCallbackHandler(Class<?> callbackHandler);

    /**
     * Gets the module name.
     * @return the module name
     */
    public String getModuleName();

    /**
     * Sets the module name.
     * @param moduleName the module name
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setModuleName(String moduleName);

    /*
    public Set<String> getRolesAllowed();
    public SecurityModel setRolesAllowed(Set<String> rolesAllowed);
    public String getRunAs();
    public SecurityModel setRunAs(String runAs);
    */

    /**
     * Gets the properties.
     * @return the properties
     */
    public PropertiesModel getProperties();

    /**
     * Sets the properties.
     * @param properties the properties
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setProperties(PropertiesModel properties);

}
