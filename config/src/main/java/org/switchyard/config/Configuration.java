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

package org.switchyard.config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * The central and most important interface of the Configuration API.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Configuration {

    /**
     * Gets the name of this config.
     * @return the name
     */
    public String getName();

    /**
     * Gets the qualified name of this config.
     * @return the qualified name
     */
    public QName getQName();

    /**
     * Gets all namespaces contained by this config and all it's children.
     * @return the namespaces
     */
    public Set<String> getNamespaces();

    /**
     * Gets a mapping of all namespaces to qname prefixes contained by this config and all it's children.
     * @return the mapping
     */
    public Map<String,String> getNamespacePrefixMap();

    /**
     * Gets a mapping of all qname prefixes to namespaces contained by this config and all it's children.
     * @return the mapping
     */
    public Map<String,String> getPrefixNamespaceMap();

    /**
     * Gets the value of this config.
     * @return the value
     */
    public String getValue();

    /**
     * Sets the value of this config.
     * @param value the value
     * @return this config (useful for chaining)
     */
    public Configuration setValue(String value);

    /**
     * Gets the list of attribute names of this config.
     * @return the attribute names
     */
    public List<String> getAttributeNames();

    /**
     * Gets the list of qualified attribute names of this config.
     * @return the qualified attribute names
     */
    public List<QName> getAttributeQNames();

    /**
     * If this config has the specified attribute by name.
     * @param name the name of the attribute
     * @return true if the attribute is present
     */
    public boolean hasAttribute(String name);

    /**
     * If this config has the specified attribute by qualified name.
     * @param qname the qualified name of the attribute
     * @return true if the attribute is present
     */
    public boolean hasAttribute(QName qname);

    /**
     * Gets the specified attribute by name.
     * @param name the name of the attribute
     * @return the attribute, or null if it not present
     */
    public String getAttribute(String name);

    /**
     * Gets the specified attribute by qualified name.
     * @param qname the qualified name of the attribute
     * @return the attribute, or null if it not present
     */
    public String getAttribute(QName qname);

    /**
     * Sets the specified attribute by name.
     * @param name the name of the attribute
     * @param value the value for the attribute (a null value will remove the attribute)
     * @return this Configuration (useful for chaining)
     */
    public Configuration setAttribute(String name, String value);

    /**
     * Sets the specified attribute by qualified name.
     * @param qname the qualified name of the attribute
     * @param value the value for the attribute (a null value will remove the attribute)
     * @return this Configuration (useful for chaining)
     */
    public Configuration setAttribute(QName qname, String value);

    /**
     * If this config has a parent config.
     * @return true if there is a parent config
     */
    public boolean hasParent();

    /**
     * Gets the parent config, if it exists.<p/>
     * 
     * <i>Guaranteed:</i> child.getParent().equals(child.getParent())<br/>
     * <i>Guaranteed:</i> child.getParent() == child.getParent()<br/>
     * <i>Guaranteed:</i> parent; child = parent.getFirstChild("foo"); parent.equals(child.getParent())<br/>
     * <i><b>NOT</b> guaranteed:</i> parent; child = parent.getFirstChild("foo"); parent == child.getParent()
     * 
     * @return the parent Configuration, or null if there is no parent
     */
    public Configuration getParent();

    /**
     * If this config has any children configs.
     * @return true if this config has children configs
     */
    public boolean hasChildren();

    /**
     * If this config has any children configs with the specified name.
     * @param name the name of the children configs
     * @return true if this config has children configs with the specified name
     */
    public boolean hasChildren(String name);

    /**
     * If this config has any children configs with the specified qualified name.
     * @param qname the qualified name of the children configs
     * @return true if this config has children configs with the specified name
     */
    public boolean hasChildren(QName qname);

    /**
     * Gets all children configs of this config.
     * @return all children configs
     */
    public List<Configuration> getChildren();

    /**
     * Gets all children configs of this config with the specified name.
     * @param name the name of the children configs
     * @return all children configs with the specified name
     */
    public List<Configuration> getChildren(String name);

    /**
     * Gets all children configs of this config with a name that starts with the specified prefix.
     * @param name the prefix to match against
     * @return all children configs with the specified prefix
     */
    public List<Configuration> getChildrenStartsWith(String name);

    /**
     * Gets all children configs of this config with a name that matches the specified regexp.
     * @param regexp the regexp to match against
     * @return all children configs with a matching name
     */
    public List<Configuration> getChildrenMatches(String regexp);

    /**
     * Gets all children configs of this config with the specified qualified name.
     * @param qname the qualified name of the children configs
     * @return all children configs with the specified qualified name
     */
    public List<Configuration> getChildren(QName qname);

    /**
     * Gets the first child config with the specified name.
     * @param name name of the child config
     * @return the first child config with the specified name
     */
    public Configuration getFirstChild(String name);

    /**
     * Gets the first child config with a specified name that starts with the specified prefix.
     * @param name the prefix to match against
     * @return the first child config with the specified prefix
     */
    public Configuration getFirstChildStartsWith(String name);

    /**
     * Gets the first child config with the specified qualified name.
     * @param qname qualified name of the child config
     * @return the first child config with the specified qualified name
     */
    public Configuration getFirstChild(QName qname);

    /**
     * Adds a child config to this config.
     * @param child the child to add
     * @return this config (useful for chaining)
     */
    public Configuration addChild(Configuration child);

    /**
     * Removes all children configs from this config.
     * @return this config (useful for chaining)
     */
    public Configuration removeChildren();

    /**
     * Removes all children configs with the specified name.
     * @param name the name of the child configs to remove
     * @return this config (useful for chaining)
     */
    public Configuration removeChildren(String name);

    /**
     * Removes all children configs of this config with a name that starts with the specified prefix.
     * @param name the prefix to match against
     * @return this config (useful for chaining)
     */
    public Configuration removeChildrenStartsWith(String name);

    /**
     * Removes all children configs of this config with a name that matches the specified regexp.
     * @param regexp the regexp to match against
     * @return this config (useful for chaining)
     */
    public Configuration removeChildrenMatches(String regexp);

    /**
     * Removes all children configs with the specified qualified name.
     * @param qname the qualified name of the child configs to remove
     * @return this config (useful for chaining)
     */
    public Configuration removeChildren(QName qname);

    /**
     * Gets the names of the children used to order them by.
     * @return the child config names used for ordering
     */
    public String[] getChildrenOrder();

    /**
     * Sets the names of the children used to order them by.
     * @param childrenOrder the child config names used for ordering
     * @return this config (useful for chaining)
     */
    public Configuration setChildrenOrder(String... childrenOrder);

    /**
     * Orders the child configs based on {@link #getChildrenOrder()}.
     * Same as {@link #orderChildren(boolean)} with recursive as true.
     * @return this config (useful for chaining)
     */
    public Configuration orderChildren();

    /**
     * Orders the child configs based on {@link #getChildrenOrder()}.
     * @param recursive whether the children/grandchildren/great-grandchildren/etc should also be ordered
     * @return this config (useful for chaining)
     */
    public Configuration orderChildren(boolean recursive);

    /**
     * Copies this config.
     * @return a copy of this config
     */
    public Configuration copy();

    /**
     * Normalizes this config. What this actually means is implementation-specific, but useful as their are known times when it should be done.
     * @return this config (useful for chaining)
     */
    public Configuration normalize();

    /**
     * Gets a source of the underlying config structure.
     * @param keys the OutputKeys to respect
     * @return the source
     */
    public Source getSource(OutputKey... keys);

    /**
     * Gets a string of the underlying config structure.
     * @param keys the OutputKeys to respect
     * @return the string
     */
    public String getString(OutputKey... keys);

    /**
     * Writes this config out in it's native form (implementation-specific).
     * @param out the OutputStream to write to
     * @param keys the OutputKeys to respect
     * @throws IOException if a problem occurs
     */
    public void write(OutputStream out, OutputKey... keys) throws IOException;

    /**
     * Writes this config out in it's native form (implementation-specific).
     * @param writer the Writer to write to
     * @param keys the OutputKeys to respect
     * @throws IOException if a problem occurs
     */
    public void write(Writer writer, OutputKey... keys) throws IOException;

}
