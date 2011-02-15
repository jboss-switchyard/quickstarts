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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;

/**
 * BaseModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseModel implements Model {

    private Configuration _config;
    private Descriptor _desc;
    private Map<Configuration,Model> _config_model_map;
    private Model _parent;

    public BaseModel(QName qname) {
        this(Configurations.create(qname));
    }

    public BaseModel(Configuration config) {
        this(config, null);
    }

    public BaseModel(Configuration config, Descriptor desc) {
        _config = config;
        _desc = desc != null ? desc : new Descriptor();
        _config_model_map = new WeakHashMap<Configuration,Model>();
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

    
    protected final boolean hasParentModel() {
        return getParentModel() != null;
    }

    /**
     * Gets the parent Model, if it exists.<p/>
     * 
     * <i>Guaranteed:</i> child.getParentModel().equals(child.getParentModel())<br/>
     * <i>Guaranteed:</i> child.getParentModel() == child.getParentModel()<br/>
     * <i>Guaranteed:</i> parent; child = parent.getFirstChildModel("foo"); parent.equals(child.getParentModel())<br/>
     * <i><b>NOT</b> guaranteed:</i> parent; child = parent.getFirstChildModel("foo"); parent == child.getParentModel()
     * 
     * @return the parent Model, or null if there is no parent
     */
    @Override
    public final Model getParentModel() {
        if (_parent == null) {
            Configuration parent_config = _config.getParent();
            if (parent_config != null) {
                _parent = readModel(parent_config);
            }
        }
        return _parent;
    }

    @Override
    public final List<Model> getChildModels() {
        List<Model> child_models = new ArrayList<Model>();
        List<Configuration> child_configs =_config.getChildren();
        if (child_configs != null) {
            for (Configuration child_config : child_configs) {
                if (child_config != null) {
                    Model child_model = readModel(child_config);
                    if (child_model != null) {
                        child_models.add(child_model);
                    }
                }
            }
        }
        return child_models;
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

    protected final synchronized Model readModel(Configuration config) {
        if (config != null) {
            Model model = _config_model_map.get(config);
            if (model == null) {
                Marshaller marsh = ModelResource.getMarshaller(config, _desc);
                if (marsh != null) {
                    model = marsh.read(config);
                    if (model != null) {
                        _config_model_map.put(config, model);
                    }
                    return model;
                }
            }
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
            _config.removeChildren(child_config.getQName());
            _config.addChild(child_config);
        }
        return this;
    }

    protected final String[] getModelChildrenOrder() {
        return _config.getChildrenOrder();
    }

    protected final Model setModelChildrenOrder(String... childrenOrder) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_config == null) ? 0 : _config.hashCode());
        result = prime * result + ((_desc == null) ? 0 : _desc.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseModel other = (BaseModel)obj;
        if (_config == null) {
            if (other._config != null) {
                return false;
            }
        } else if (!_config.equals(other._config)) {
            return false;
        }
        if (_desc == null) {
            if (other._desc != null) {
                return false;
            }
        } else if (!_desc.equals(other._desc)) {
            return false;
        }
        return true;
    }

}
