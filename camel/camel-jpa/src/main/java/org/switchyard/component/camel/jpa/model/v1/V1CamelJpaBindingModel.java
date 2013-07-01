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
package org.switchyard.component.camel.jpa.model.v1;

import java.net.URI;
import java.util.List;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.jpa.model.CamelJpaBindingModel;
import org.switchyard.component.camel.jpa.model.CamelJpaConsumerBindingModel;
import org.switchyard.component.camel.jpa.model.CamelJpaProducerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

import static org.switchyard.component.camel.jpa.model.Constants.JPA_NAMESPACE_V1;

/**
 * Implementation of JPA configuration binding model.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaBindingModel extends V1BaseCamelBindingModel 
    implements CamelJpaBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String JPA = "jpa";

    private static final String ENTITY_CLASS_NAME = "entityClassName";
    private static final String PERSISTENCE_UNIT = "persistenceUnit";
    private static final String TRANSACTION_MANAGER = "transcationManager";

    private CamelJpaConsumerBindingModel _consume;
    private CamelJpaProducerBindingModel _produce;

    /**
     * Creates a binding.
     */
    public V1CamelJpaBindingModel() {
        super(JPA, JPA_NAMESPACE_V1);

        setModelChildrenOrder(ENTITY_CLASS_NAME, PERSISTENCE_UNIT, TRANSACTION_MANAGER,
            CONSUME, PRODUCE);
    }

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelJpaBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getEntityClassName() {
        return getConfig(ENTITY_CLASS_NAME);
    }

    @Override
    public V1CamelJpaBindingModel setEntityClassName(String entityClassName) {
        return setConfig(ENTITY_CLASS_NAME, entityClassName);
    }

    @Override
    public String getPersistenceUnit() {
        return getConfig(PERSISTENCE_UNIT);
    }

    @Override
    public V1CamelJpaBindingModel setPersistenceUnit(String persistenceUnit) {
        return setConfig(PERSISTENCE_UNIT, persistenceUnit);
    }

    @Override
    public String getTransactionManager() {
        return getConfig(TRANSACTION_MANAGER);
    }

    @Override
    public V1CamelJpaBindingModel setTransactionManager(String transactionManager) {
        return setConfig(TRANSACTION_MANAGER, transactionManager);
    }

    @Override
    public CamelJpaConsumerBindingModel getConsumer() {
        if (_consume == null) {
            Configuration config = getModelConfiguration().getFirstChild(CONSUME);
            _consume = new V1CamelJpaConsumerBindingModel(config,
                getModelDescriptor());
        }
        return _consume;
    }

    @Override
    public V1CamelJpaBindingModel setConsumer(CamelJpaConsumerBindingModel consumer) {
        Configuration config = getModelConfiguration().getFirstChild(CONSUME);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(CONSUME);
            getModelConfiguration().addChild(((V1CamelJpaConsumerBindingModel) consumer)
                .getModelConfiguration());
        } else {
            setChildModel((V1CamelJpaConsumerBindingModel) consumer);
        }
        _consume = consumer;
        return this;
    }

    @Override
    public CamelJpaProducerBindingModel getProducer() {
        if (_produce == null) {
            Configuration config = getModelConfiguration().getFirstChild(PRODUCE);
            _produce = new V1CamelJpaProducerBindingModel(config,
                getModelDescriptor());
        }
        return _produce;
    }

    @Override
    public V1CamelJpaBindingModel setProducer(CamelJpaProducerBindingModel producer) {
        Configuration config = getModelConfiguration().getFirstChild(PRODUCE);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(PRODUCE);
            getModelConfiguration().addChild(((V1CamelJpaProducerBindingModel) producer)
                .getModelConfiguration());
        } else {
            setChildModel((V1CamelJpaProducerBindingModel) producer);
        }
        _produce = producer;
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = JPA + "://" + getEntityClassName();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, ENTITY_CLASS_NAME);

        return URI.create(baseUri + UnsafeUriCharactersEncoder.encode(queryStr.toString()));
    }

}
