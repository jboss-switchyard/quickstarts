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
package org.switchyard.config.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.Descriptor;
import org.switchyard.config.QNames;

/**
 * BaseModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseModel implements Model {

    private Configuration _config;
    private Descriptor _desc;
    private ModelResource _res;

    public BaseModel(String name) {
        this(Configurations.create(name));
    }

    public BaseModel(QName qname) {
        this(Configurations.create(qname));
    }

    public BaseModel(Configuration config) {
        this(config, new Descriptor());
    }

    public BaseModel(Configuration config, Descriptor desc) {
        _config = config;
        _desc = desc;
        _res = new ModelResource(_desc);
    }

    @Override
    public final Configuration getModelConfiguration() {
        return _config;
    }

    @Override
    public final Descriptor getModelDescriptor() {
        return _desc;
    }

    @Override
    public final String getModelNamespace() {
        return getModelNamespace(_config);
    }

    private String getModelNamespace(Configuration config) {
        if (config != null) {
            QName qname = config.getQName();
            if (qname != null) {
                return qname.getNamespaceURI();
            }
        }
        return null;
    }

    @Override
    public final ModelMarshaller getModelMarshaller() {
        return getModelMarshaller(_config);
    }

    protected final String getModelValue() {
        return getModelConfiguration().getValue();
    }

    protected final Model setModelValue(String value) {
        getModelConfiguration().setValue(value);
        return this;
    }

    protected final ModelMarshaller getModelMarshaller(Configuration config) {
        String namespace = getModelNamespace(config);
        if (namespace != null) {
            return (ModelMarshaller)_res.getModelMarshaller(namespace);
        }
        return null;
    }

    protected final String getModelAttribute(String name) {
        return getModelConfiguration().getAttribute(name);
    }

    protected final Model setModelAttribute(String name, String value) {
        getModelConfiguration().setAttribute(name, value);
        return this;
    }

    protected final String getModelAttribute(QName qname) {
        return getModelConfiguration().getAttribute(qname);
    }

    protected final Model setModelAttribute(QName qname, String value) {
        getModelConfiguration().setAttribute(qname, value);
        return this;
    }

    protected final Model getFirstChildModel(String name) {
        return getFirstChildModel(name, _config);
    }

    protected final Model getFirstChildModel(String name, Configuration config) {
        Configuration child_config = config.getFirstChild(name);
        if (child_config != null) {
            return readModel(child_config);
        }
        return null;
    }

    protected final Model getFirstChildModelStartsWith(String name) {
        return getFirstChildModelStartsWith(name, _config);
    }

    protected final Model getFirstChildModelStartsWith(String name, Configuration config) {
        Configuration child_config = config.getFirstChildStartsWith(name);
        if (child_config != null) {
            return readModel(child_config);
        }
        return null;
    }

    protected final Model readModel() {
        return readModel(_config);
    }

    protected final Model readModel(Configuration config) {
        ModelMarshaller marsh = getModelMarshaller(config);
        if (marsh != null) {
            return marsh.read(config);
        }
        return null;
    }

    protected Model addChildModel(Model child) {
        return addChildModel(child, _config);
    }

    protected Model addChildModel(Model child, Configuration config) {
        if (child != null) {
            config.addChild(child.getModelConfiguration());
        }
        return this;
    }

    protected Model setChildModel(Model child) {
        return setChildModel(child, _config);
    }

    protected Model setChildModel(Model child, Configuration config) {
        if (child != null) {
            Configuration child_config = child.getModelConfiguration();
            config.removeChildren(child_config.getName());
            config.addChild(child_config);
        }
        return this;
    }

    protected String[] getChildrenGroups() {
        return _config.getChildrenGroups();
    }

    protected Model setChildrenGroups(String... childrenGroups) {
        _config.setChildrenGroups(childrenGroups);
        return this;
    }

    @Override
    public final String getName() {
        return getModelAttribute("name");
    }

    @Override
    public final Model setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    @Override
    public final QName getQName() {
        return QNames.create(getName());
    }

    @Override
    public final Model setQName(QName qname) {
        return setName(qname != null ? qname.toString() : null);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        getModelConfiguration().write(out);
    }

    @Override
    public void write(Writer writer) throws IOException {
        getModelConfiguration().write(writer);
    }

    @Override
    public String toString() {
        return _config.toString();
    }

}
