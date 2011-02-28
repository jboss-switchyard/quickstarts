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
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationResource;

/**
 * An abstract representation of a Model, containing many helper methods, as well as default implementations for some of the defined methods..
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseModel implements Model {

    private Configuration _config;
    private Descriptor _desc;
    private Map<Configuration,Model> _config_model_map;
    private Model _parent;

    protected BaseModel(QName qname) {
        this(new ConfigurationResource().pull(qname));
    }

    protected BaseModel(Configuration config) {
        this(config, null);
    }

    protected BaseModel(Configuration config, Descriptor desc) {
        _config = config;
        _desc = desc != null ? desc : new Descriptor();
        _config_model_map = new WeakHashMap<Configuration,Model>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Configuration getModelConfiguration() {
        return _config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Descriptor getModelDescriptor() {
        return _desc;
    }

    /**
     * Gets the wrapped config's value.
     * @return the value
     */
    protected final String getModelValue() {
        return getModelConfiguration().getValue();
    }

    /**
     * Sets the wrapped config's value.
     * @param value the value
     * @return this model (useful for chaining)
     */
    protected final Model setModelValue(String value) {
        getModelConfiguration().setValue(value);
        return this;
    }

    /**
     * Gets the wrapped config's value for an attribute with a specified name.
     * @param name the attribute name
     * @return the attribute value
     */
    protected final String getModelAttribute(String name) {
        return getModelConfiguration().getAttribute(name);
    }

    /**
     * Sets the wrapped config's value for an attribute with a specified name.
     * @param name the attribute name
     * @param value the attribute value (null means "remove the attribute")
     * @return this model (useful for chaining)
     */
    protected final Model setModelAttribute(String name, String value) {
        getModelConfiguration().setAttribute(name, value);
        return this;
    }

    /**
     * Whether or not this model has a parent model.
     * @return true if a parent model exists
     */
    protected final boolean hasParentModel() {
        return getModelParent() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Model getModelParent() {
        if (_parent == null) {
            Configuration parent_config = _config.getParent();
            if (parent_config != null) {
                _parent = readModel(parent_config);
            }
        }
        return _parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Model> getModelChildren() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Validation validateModel() {
        Schema schema = _desc.getSchema(_config);
        if (schema != null) {
            Validator validator = schema.newValidator();
            Source source = _config.getSource();
            try {
                validator.validate(source);
            } catch (Throwable t) {
                return new Validation(t);
            }
        } else {
            return new Validation(false, "schema == null");
        }
        return new Validation(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModelValid() {
        return validateModel().isValid();
    }

    /**
     * Gets the wrapped config's value for an attribute with a specified qualified name.
     * @param qname the attribute qualified name
     * @return the attribute value
     */
    protected final String getModelAttribute(QName qname) {
        return getModelConfiguration().getAttribute(qname);
    }

    /**
     * Sets the wrapped config's value for an attribute with a specified qualified name.
     * @param qname the attribute qualified name
     * @param value the attribute value (null means "remove the attribute")
     * @return this model (useful for chaining)
     */
    protected final Model setModelAttribute(QName qname, String value) {
        getModelConfiguration().setAttribute(qname, value);
        return this;
    }

    /**
     * Gets the first child model with the specified name.
     * @param name name of the child model
     * @return the first child model with the specified name
     */
    protected final Model getFirstChildModel(String name) {
        Configuration child_config = _config.getFirstChild(name);
        if (child_config != null) {
            return readModel(child_config);
        }
        return null;
    }

    /**
     * Gets the first child model with a specified name that starts with the specified prefix.
     * @param name the prefix to match against
     * @return the first child model with the specified prefix
     */
    protected final Model getFirstChildModelStartsWith(String name) {
        Configuration child_config = _config.getFirstChildStartsWith(name);
        if (child_config != null) {
            return readModel(child_config);
        }
        return null;
    }

    /**
     * Reads (constructs) a Model based on the specified Configuration.
     * @param config the Configuration
     * @return the new Model
     */
    protected final synchronized Model readModel(Configuration config) {
        if (config != null) {
            Model model = _config_model_map.get(config);
            if (model == null) {
                Marshaller marsh = _desc.getMarshaller(config);
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

    /**
     * Adds a child model to this model.
     * @param child the child to add
     * @return this model (useful for chaining)
     */
    protected final Model addChildModel(Model child) {
        if (child != null) {
            _config.addChild(child.getModelConfiguration());
        }
        return this;
    }

    /**
     * First removes all child models with a wrapped Configuration which has a matching qualified name, then adds the single, specified child model.
     * @param child the child model to add
     * @return this model (useful for chaining)
     */
    protected final Model setChildModel(Model child) {
        if (child != null) {
            Configuration child_config = child.getModelConfiguration();
            _config.removeChildren(child_config.getQName());
            _config.addChild(child_config);
        }
        return this;
    }

    /**
     * Gets the names of the children (based on their wrapped Configurations) used to order them by.
     * @return the child model's names (based on their wrapped Configurations) used for ordering
     */
    protected final String[] getModelChildrenOrder() {
        return _config.getChildrenOrder();
    }

    /**
     * Sets the names of the children (based on their wrapped Configurations) used to order them by.
     * @param childrenOrder the child model names (based on their wrapped Configurations) used for ordering
     * @return this model (useful for chaining)
     */
    protected final Model setModelChildrenOrder(String... childrenOrder) {
        _config.setChildrenOrder(childrenOrder);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream out) throws IOException {
        getModelConfiguration().write(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Writer writer) throws IOException {
        getModelConfiguration().write(writer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _config.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_config == null) ? 0 : _config.hashCode());
        result = prime * result + ((_desc == null) ? 0 : _desc.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
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
