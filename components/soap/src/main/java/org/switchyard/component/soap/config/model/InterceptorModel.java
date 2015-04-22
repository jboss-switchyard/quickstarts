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
package org.switchyard.component.soap.config.model;

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * A Interceptor Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface InterceptorModel extends NamedModel {

    /**
     * The interceptor XML element.
     */
    public static final String INTERCEPTOR = "interceptor";

    /**
     * Gets the Interceptor class.
     * @param loader the ClassLoader to use
     * @return the Interceptor class
     */
    public Class<?> getClazz(ClassLoader loader);

    /**
     * Sets the Interceptor class.
     * @param clazz the Interceptor class
     * @return this InterceptorModel (useful for chaining)
     */
    public InterceptorModel setClazz(Class<?> clazz);

    /**
     * Gets the child properties model.
     * @return the child properties model
     */
    public PropertiesModel getProperties();

    /**
     * Sets the child properties model.
     * @param properties the child properties model
     * @return this InterceptorModel (useful for chaining)
     */
    public InterceptorModel setProperties(PropertiesModel properties);

}
