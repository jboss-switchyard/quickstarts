/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.config.model.Model;

/**
 * A ExtraJaxbClass Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface ExtraJaxbClassModel extends Model {

    /**
     * The extraJaxbClass XML element.
     */
    public static final String EXTRA_JAXB_CLASS = "extraJaxbClass";

    /**
     * Gets the extra JAXB class.
     * @param loader the ClassLoader to use
     * @return the extra JAXB class
     */
    public Class<?> getClazz(ClassLoader loader);

    /**
     * Sets the extra JAXB class.
     * @param clazz the extra JAXB class
     * @return this ExtraJaxbClassModel (useful for chaining)
     */
    public ExtraJaxbClassModel setClazz(Class<?> clazz);

}
