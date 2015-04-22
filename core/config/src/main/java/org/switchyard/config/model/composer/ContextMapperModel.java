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

package org.switchyard.config.model.composer;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;

/**
 * The "contextMapper" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ContextMapperModel extends Model {

    /** contextMapper variable. */
    public static final String CONTEXT_MAPPER = "contextMapper";

    /**
     * Gets the parent binding model.
     * @return the parent binding model
     */
    public BindingModel getBindingModel();

    /**
     * Gets the fully qualified class name of ContextMapper.
     * @return the fully qualified class name of ContextMapper
     */
    public String getClazz();

    /**
     * Sets the fully qualified class name of ContextMapper.
     * @param clazz the fully qualified class name of ContextMapper
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setClazz(String clazz);

    /**
     * Gets the comma-separated list of regex property includes.
     * @return the comma-separated list of regex property includes
     */
    public String getIncludes();

    /**
     * Sets the comma-separated list of regex property includes.
     * @param includes the comma-separated list of regex property includes
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setIncludes(String includes);

    /**
     * Gets the comma-separated list of regex property excludes.
     * @return the comma-separated list of regex property excludes
     */
    public String getExcludes();

    /**
     * Sets the comma-separated list of regex property excludes.
     * @param excludes the comma-separated list of regex property excludes
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setExcludes(String excludes);

    /**
     * Gets the comma-separated list of regex property include namespaces.
     * @return the comma-separated list of regex property include namespaces
     */
    public String getIncludeNamespaces();

    /**
     * Sets the comma-separated list of regex property include namespaces.
     * @param includeNamespaces the comma-separated list of regex property include namespaces
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setIncludeNamespaces(String includeNamespaces);

    /**
     * Gets the comma-separated list of regex property exclude namespace.
     * @return the comma-separated list of regex property exclude namespace
     */
    public String getExcludeNamespaces();

    /**
     * Sets the comma-separated list of regex property exclude namespace.
     * @param excludeNamespaces the comma-separated list of regex property exclude namespace
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setExcludeNamespaces(String excludeNamespaces);

}
