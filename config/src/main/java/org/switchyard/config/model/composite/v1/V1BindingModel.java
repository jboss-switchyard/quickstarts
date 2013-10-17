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

package org.switchyard.config.model.composite.v1;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.SCANamespace;
import org.switchyard.config.model.selector.OperationSelectorModel;

/**
 * A version 1 BindingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BindingModel extends BaseTypedModel implements BindingModel {

    private OperationSelectorModel _operationSelector;
    private ContextMapperModel _contextMapper;
    private MessageComposerModel _messageComposer;

    /**
     * Constructs a new V1BindingModel of the specified "type".
     * @param type the "type" of BindingModel
     */
    public V1BindingModel(String type) {
        super(new QName(SCANamespace.DEFAULT.uri(), BindingModel.BINDING + '.' + type));
    }
    
    /**
     * Constructs a new V1BindingModel of the specified "type" with the specified namespace.
     * @param type the "type" of BindingModel
     * @param namespace binding namespace
     */
    public V1BindingModel(String type, String namespace) {
        super(new QName(namespace, BindingModel.BINDING + '.' + type));
    }

    /**
     * Constructs a new V1BindingModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute("name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BindingModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Model setModelChildrenOrder(String... childrenOrder) {
        Set<String> mco = new LinkedHashSet<String>();
        mco.add(OperationSelectorModel.OPERATION_SELECTOR + ".*");
        mco.add(ContextMapperModel.CONTEXT_MAPPER);
        mco.add(MessageComposerModel.MESSAGE_COMPOSER);
        if (childrenOrder != null) {
            mco.addAll(Arrays.asList(childrenOrder));
        }
        super.setModelChildrenOrder(mco.toArray(new String[mco.size()]));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeServiceModel getService() {
        return isServiceBinding() ? (CompositeServiceModel)getModelParent() : null;
    }
    
    @Override
    public CompositeReferenceModel getReference() {
        return isReferenceBinding() ? (CompositeReferenceModel)getModelParent() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationSelectorModel getOperationSelector() {
        if (_operationSelector == null) {
            _operationSelector = (OperationSelectorModel)this.getFirstChildModelStartsWith(OperationSelectorModel.OPERATION_SELECTOR);
        }
        return _operationSelector;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BindingModel setOperationSelector(OperationSelectorModel model) {
        setChildModel(model);
        _operationSelector = model;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapperModel getContextMapper() {
        if (_contextMapper == null) {
            _contextMapper = (ContextMapperModel)getFirstChildModel(ContextMapperModel.CONTEXT_MAPPER);
        }
        return _contextMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageComposerModel getMessageComposer() {
        if (_messageComposer == null) {
            _messageComposer = (MessageComposerModel)getFirstChildModel(MessageComposerModel.MESSAGE_COMPOSER);
        }
        return _messageComposer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BindingModel setContextMapper(ContextMapperModel model) {
        _contextMapper = model;
        setChildModel(model);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BindingModel setMessageComposer(MessageComposerModel model) {
        _messageComposer = model;
        setChildModel(model);
        return this;
    }

    @Override
    public boolean isServiceBinding() {
        return (getModelParent() instanceof CompositeServiceModel);
    }

    @Override
    public boolean isReferenceBinding() {
        return (getModelParent() instanceof CompositeReferenceModel);
    }

}
