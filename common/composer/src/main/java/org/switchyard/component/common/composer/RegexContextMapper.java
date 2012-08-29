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
