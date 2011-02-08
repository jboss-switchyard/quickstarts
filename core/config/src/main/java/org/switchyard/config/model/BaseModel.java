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

/**
 * BaseModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseModel implements Model {

    private Configuration _config;
    private Descriptor _desc;

    public BaseModel(String name) {
        this(Configurations.create(name));
    }

    public BaseModel(QName qname) {
        this(Configurations.create(qname));
    }

    public BaseModel(Configuration config) {
        this(config, null);
    }

    public BaseModel(Configuration config, Descriptor desc) {
        _config = config;
        _desc = desc != null ? desc : new Descriptor();
    }

    @Override
    public final Configuration getModelConfiguration() {
        return _config;
    }

    @Override
    public final Descriptor getModelDescriptor() {
        return _desc;
    }

    protected final String getModelValue() {
        return getModelConfiguration().getValue();
    }

    protected final Model setModelValue(String value) {
        getModelConfiguration().setValue(value);
        return this;
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
        Configuration child_config = _config.getFirstChild(name);
        if (child_config != null) {
            return readModel(child_config);
        }
        return null;
    }

    protected final Model getFirstChildModelStartsWith(String name) {
        Configuration child_config = _config.getFirstChildStartsWith(name);
        if (child_config != null) {
            return readModel(child_config);
        }
        return null;
    }

    protected final Model readModel(Configuration config) {
        ModelMarshaller marsh = ModelResource.getModelMarshaller(config, _desc);
        if (marsh != null) {
            return marsh.read(config);
        }
        return null;
    }

    protected final Model addChildModel(Model child) {
        if (child != null) {
            _config.addChild(child.getModelConfiguration());
        }
        return this;
    }

    protected final Model setChildModel(Model child) {
        if (child != null) {
            Configuration child_config = child.getModelConfiguration();
            _config.removeChildren(child_config.getName());
            _config.addChild(child_config);
        }
        return this;
    }

    protected final String[] getChildrenOrder() {
        return _config.getChildrenOrder();
    }

    protected final Model setChildrenOrder(String... childrenOrder) {
        _config.setChildrenOrder(childrenOrder);
        return this;
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
