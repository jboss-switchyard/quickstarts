/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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
