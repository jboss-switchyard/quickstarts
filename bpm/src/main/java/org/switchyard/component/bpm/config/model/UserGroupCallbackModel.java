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
