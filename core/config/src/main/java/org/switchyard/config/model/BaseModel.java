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
package org.switchyard.config.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.config.OutputKey;

/**
 * An abstract representation of a Model, containing many helper methods, as well as default implementations for some of the defined methods..
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseModel implements Model {

    private final Configuration _config;
    private final Descriptor _desc;
    private Model _parent;

    protected BaseModel(String namespace, String name) {
        this(XMLHelper.createQName(namespace, name));
    }

    protected BaseModel(QName qname) {
        this(new ConfigurationPuller().pull(qname));
    }

    protected BaseModel(Configuration config) {
        this(config, null);
    }

    protected BaseModel(Configuration config, Descriptor desc) {
        _config = config;
        _desc = desc != null ? desc : new Descriptor();
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
     * Gets the wrapped configuration namespace uri.
     * @return Namespace uri of this model instance.
     */
    protected final String getNamespaceURI() {
        return getModelConfiguration().getQName().getNamespaceURI();
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
     * Gets the wrapped config's value for an attribute with a specified qualified name.
     * @param qname the attribute qualified name
     * @return the attribute value
     */
    protected final String getModelAttribute(QName qname) {
        return getModelConfiguration().getAttribute(qname);
    }

    /**
     * Gets the wrapped config's value for an attribute with a specified name, as a qualified name.
     * @param name the attribute name
     * @return the attribute value, as a qualified name
     */
    protected final QName getModelAttributeAsQName(String name) {
        return getModelConfiguration().getAttributeAsQName(name);
    }

    /**
     * Gets the wrapped config's value for an attribute with a specified qualified name, as a qualified name.
     * @param qname the attribute qualified name
     * @return the attribute value, as a qualified name
     */
    protected final QName getModelAttributeAsQName(QName qname) {
        return getModelConfiguration().getAttributeAsQName(qname);
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
     * Sets the wrapped config's value for an attribute with a specified name, as a qualified name.
     * @param name the attribute name
     * @param value the attribute value, as a qualified name (null means "remove the attribute")
     * @return this model (useful for chaining)
     */
    protected final Model setModelAttributeAsQName(String name, QName value) {
        getModelConfiguration().setAttributeAsQName(name, value);
        return this;
    }

    /**
     * Sets the wrapped config's value for an attribute with a specified qualified name, as a qualified name.
     * @param qname the attribute qualified name
     * @param value the attribute value, as a qualified name (null means "remove the attribute")
     * @return this model (useful for chaining)
     */
    protected final Model setModelAttributeAsQName(QName qname, QName value) {
        getModelConfiguration().setAttributeAsQName(qname, value);
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
    public final Model getModelRoot() {
        Model root = this;
        while (root.getModelParent() != null) {
            root = root.getModelParent();
        }
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModelRootNamespace() {
        //return getModelRoot().getModelConfiguration().getQName().getNamespaceURI();
        // the line below is faster than the line above, as it won't invoke readModel
        return _config.getRootNamespace();
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
        Validator validator = _desc.getValidator(_config);
        if (validator != null) {
            Source source = _config.getSource();
            try {
                validator.validate(source);
            } catch (Throwable t) {
                return new Validation(getClass(), t);
            }
        } else {
            return new Validation(getClass(), false, "validator == null");
        }
        return new Validation(getClass(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model assertModelValid() {
        validateModel().assertValid();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModelValid() {
        return validateModel().isValid();
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
            Marshaller marsh = _desc.getMarshaller(config);
            if (marsh != null) {
                return marsh.read(config);
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
    protected Model setModelChildrenOrder(String... childrenOrder) {
        String[] existingOrder = getModelChildrenOrder();
        if (existingOrder != null && existingOrder.length > 0) {
            List<String> orderList = new ArrayList<String>();
            orderList.addAll(Arrays.asList(existingOrder));
            orderList.addAll(Arrays.asList(childrenOrder));
            _config.setChildrenOrder(orderList.toArray(new String[orderList.size()]));
        } else {
            _config.setChildrenOrder(childrenOrder);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model orderModelChildren() {
        primeModelChildren();
        getModelConfiguration().orderChildren();
        return this;
    }

    /**
     * Recursively iterates over the children, which will force instantiation of a Model for every child Configuration.
     * @return this model (useful for chaining)
     */
    protected final Model primeModelChildren() {
        primeModelChildren(this);
        return this;
    }

    private final void primeModelChildren(Model model) {
        for (Model child : model.getModelChildren()) {
            primeModelChildren(child);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream out, OutputKey... keys) throws IOException {
        getModelConfiguration().write(out, keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Writer writer, OutputKey... keys) throws IOException {
        getModelConfiguration().write(writer, keys);
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
