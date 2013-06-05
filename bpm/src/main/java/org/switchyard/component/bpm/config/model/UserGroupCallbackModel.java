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
package org.switchyard.component.bpm.config.model;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * A UserGroupCallback Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface UserGroupCallbackModel extends Model {

    /**
     * The userGroupCallback XML element.
     */
    public static final String USER_GROUP_CALLBACK = "userGroupCallback";

    /**
     * Gets the UserGroupCallback class.
     * @param loader the ClassLoader to use
     * @return the UserGroupCallback class
     */
    public Class<?> getClazz(ClassLoader loader);

    /**
     * Sets the UserGroupCallback class.
     * @param clazz the UserGroupCallback class
     * @return this UserGroupCallbackModel (useful for chaining)
     */
    public UserGroupCallbackModel setClazz(Class<?> clazz);

    /**
     * Gets the child properties model.
     * @return the child properties model
     */
    public PropertiesModel getProperties();

    /**
     * Sets the child properties model.
     * @param properties the child properties model
     * @return this UserGroupCallbackModel (useful for chaining)
     */
    public UserGroupCallbackModel setProperties(PropertiesModel properties);

}
