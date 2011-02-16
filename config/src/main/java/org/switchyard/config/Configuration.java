/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

/**
 * Configuration.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Configuration {

    public String getName();

    public QName getQName();

    public Set<String> getNamespaces();

    public Map<String,String> getNamespacePrefixMap();

    public Map<String,String> getPrefixNamespaceMap();

    public String getValue();

    public Configuration setValue(String value);

    public List<String> getAttributeNames();

    public List<QName> getAttributeQNames();

    public boolean hasAttribute(String name);

    public boolean hasAttribute(QName qname);

    public String getAttribute(String name);

    public String getAttribute(QName qname);

    public Configuration setAttribute(String name, String value);

    public Configuration setAttribute(QName qname, String value);

    public boolean hasParent();

    /**
     * Gets the parent Configuration, if it exists.<p/>
     * 
     * <i>Guaranteed:</i> child.getParent().equals(child.getParent())<br/>
     * <i>Guaranteed:</i> child.getParent() == child.getParent()<br/>
     * <i>Guaranteed:</i> parent; child = parent.getFirstChild("foo"); parent.equals(child.getParent())<br/>
     * <i><b>NOT</b> guaranteed:</i> parent; child = parent.getFirstChild("foo"); parent == child.getParent()
     * 
     * @return the parent Configuration, or null if there is no parent
     */
    public Configuration getParent();

    public boolean hasChildren();

    public boolean hasChildren(String name);

    public boolean hasChildren(QName qname);

    public List<Configuration> getChildren();

    public List<Configuration> getChildren(String name);

    public List<Configuration> getChildrenStartsWith(String name);

    public List<Configuration> getChildren(QName qname);

    public Configuration getFirstChild(String name);

    public Configuration getFirstChildStartsWith(String name);

    public Configuration getFirstChild(QName qname);

    public Configuration addChild(Configuration child);

    public Configuration removeChildren();

    public Configuration removeChildren(String name);

    public Configuration removeChildren(QName qname);

    public String[] getChildrenOrder();

    public Configuration setChildrenOrder(String... childrenOrder);

    public Configuration orderChildren();

    public Configuration copy();

    public Configuration normalize();

    public void write(OutputStream out) throws IOException;

    public void write(Writer writer) throws IOException;

}
