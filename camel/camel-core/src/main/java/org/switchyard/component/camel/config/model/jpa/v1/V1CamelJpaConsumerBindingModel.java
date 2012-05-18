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
package org.switchyard.component.camel.config.model.jpa.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.jpa.CamelJpaConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelScheduledBatchPollConsumer;
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
        super(new QName(V1CamelJpaBindingModel.CONSUME));
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
