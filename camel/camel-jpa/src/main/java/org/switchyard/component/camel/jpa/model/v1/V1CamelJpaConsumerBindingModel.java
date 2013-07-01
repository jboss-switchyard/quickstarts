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

import static org.switchyard.component.camel.jpa.model.Constants.JPA_NAMESPACE_V1;

import org.switchyard.component.camel.common.model.v1.V1CamelScheduledBatchPollConsumer;
import org.switchyard.component.camel.jpa.model.CamelJpaConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of jpa consumer configuration binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaConsumerBindingModel extends V1CamelScheduledBatchPollConsumer
    implements CamelJpaConsumerBindingModel {

    private static final String CONSUME_DELETE = "consumeDelete";
    private static final String CONSUME_LOCK_ENTITY = "consumeLockEntity";
    private static final String MAXIMUM_RESULTS = "maximumResults";
    private static final String QUERY = "consumer.query";
    private static final String NAMED_QUERY = "consumer.namedQuery";
    private static final String NATIVE_QUERY = "consumer.nativeQuery";
    private static final String RESULT_CLASS = "consumer.resultClass";
    private static final String TRANSACTED = "consumer.transacted";

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelJpaConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);

        setModelChildrenOrder(CONSUME_DELETE, CONSUME_LOCK_ENTITY, MAXIMUM_RESULTS, QUERY,
            NAMED_QUERY, NATIVE_QUERY, RESULT_CLASS, TRANSACTED);
    }

    /**
     * Creates new binding model.
     */
    public V1CamelJpaConsumerBindingModel() {
        super(V1CamelJpaBindingModel.CONSUME, JPA_NAMESPACE_V1);
    }

    @Override
    public Boolean isConsumeDelete() {
        return getBooleanConfig(CONSUME_DELETE);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setConsumeDelete(Boolean consumeDelete) {
        return setConfig(CONSUME_DELETE, consumeDelete);
    }

    @Override
    public Boolean isConsumeLockEntity() {
        return getBooleanConfig(CONSUME_LOCK_ENTITY);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setConsumeLockEntity(Boolean consumeLockEntity) {
        return setConfig(CONSUME_LOCK_ENTITY, consumeLockEntity);
    }

    @Override
    public Integer getMaximumResults() {
        return getIntegerConfig(MAXIMUM_RESULTS);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setMaximumResults(Integer maximumResults) {
        return setConfig(MAXIMUM_RESULTS, maximumResults);
    }

    @Override
    public String getQuery() {
        return getConfig(QUERY);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setQuery(String query) {
        return setConfig(QUERY, query);
    }

    @Override
    public String getNamedQuery() {
        return getConfig(NAMED_QUERY);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setNamedQuery(String namedQuery) {
        return setConfig(NAMED_QUERY, namedQuery);
    }

    @Override
    public String getNativeQuery() {
        return getConfig(NATIVE_QUERY);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setNativeQuery(String nativeQuery) {
        return setConfig(NATIVE_QUERY, nativeQuery);
    }

    @Override
    public String getResultClass() {
        return getConfig(RESULT_CLASS);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setResultClass(String resultClass) {
        return setConfig(RESULT_CLASS, resultClass);
    }

    @Override
    public Boolean isTransacted() {
        return getBooleanConfig(TRANSACTED);
    }

    @Override
    public V1CamelJpaConsumerBindingModel setTransacted(Boolean transacted) {
        return setConfig(TRANSACTED, transacted);
    }

}
