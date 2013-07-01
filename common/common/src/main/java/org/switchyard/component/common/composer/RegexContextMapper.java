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
package org.switchyard.component.common.composer;

import javax.xml.namespace.QName;

/**
 * Maps context properties from and to a source or target object, with the ability to selectively choose which properties with regex expressions.
 * 
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface RegexContextMapper<D extends BindingData> extends ContextMapper<D> {

    /**
     * Sets a comma-separated list of regex property includes.
     * @param includes the includes
     * @return this ContextMapper (useful for chaining)
     */
    public ContextMapper<D> setIncludes(String includes);

    /**
     * Sets a comma-separated list of regex property excludes.
     * @param excludes the excludes
     * @return this ContextMapper (useful for chaining)
     */
    public ContextMapper<D> setExcludes(String excludes);

    /**
     * Sets a comma-separated list of regex property namespace includes.
     * @param includeNamespaces the namespace includes
     * @return this ContextMapper (useful for chaining)
     */
    public ContextMapper<D> setIncludeNamespaces(String includeNamespaces);

    /**
     * Sets a comma-separated list of regex property namespace excludes.
     * @param excludeNamespaces the namespace excludes
     * @return this ContextMapper (useful for chaining)
     */
    public ContextMapper<D> setExcludeNamespaces(String excludeNamespaces);

    /**
     * Decides if the specified name passes the collective regex expressions.
     * @param name the name to test
     * @return whether the name passes the matching tests
     */
    public boolean matches(String name);

    /**
     * Decides if the specified qualified name passes the collective regex expressions.
     * @param qname the qualified name to test
     * @return whether the qualified name passes the matching tests
     */
    public boolean matches(QName qname);

}
