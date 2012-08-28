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
package org.switchyard.component.camel.config.model.file.v1;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.file.CamelFileBindingModel;
import org.switchyard.component.camel.config.model.file.CamelFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.generic.GenericFileProducerBindingModel;
import org.switchyard.component.camel.config.model.generic.v1.V1GenericFileBindingModel;
import org.switchyard.component.camel.config.model.generic.v1.V1GenericFileProducerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A binding for Camel's file component.
 * 
 * @author Daniel Bevenius
 * 
 */
public class V1CamelFileBindingModel extends V1GenericFileBindingModel implements
        CamelFileBindingModel {

    /**
     * The name of this binding type ("binding.file").
     */
    public static final String FILE = "file";

    /**
     * The name of the 'consume' element.
     */
    public static final String CONSUME = "consume";

    /**
     * In charge of parsing out consumer options
     */
    private CamelFileConsumerBindingModel _consume;

    /**
     * The name of the 'produce' element.
     */
    public static final String PRODUCE = "produce";

    /**
     * In charge of parsing out producer options
     */
    private GenericFileProducerBindingModel _produce;

    /**
     * Create a new V1CamelFileBindingModel.
     */
    public V1CamelFileBindingModel() {
        super(FILE);

        setModelChildrenOrder(CONSUME, PRODUCE);
    }

    /**
     * Constructor.
     * 
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1CamelFileBindingModel(final Configuration config,
            final Descriptor desc) {
        super(config, desc);
    }

    @Override
    public CamelFileConsumerBindingModel getConsumer() {
        if (_consume == null) {
            Configuration config = getModelConfiguration().getFirstChild(
                    CONSUME);
            _consume = new V1CamelFileConsumerBindingModel(config,
                    getModelDescriptor());
        }
        return _consume;
    }

    @Override
    public V1CamelFileBindingModel setConsumer(
            CamelFileConsumerBindingModel consumer) {
        Configuration config = getModelConfiguration().getFirstChild(CONSUME);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(CONSUME);
            getModelConfiguration().addChild(
                    ((V1CamelScheduledPollConsumer) consumer)
                            .getModelConfiguration());
        } else {
            setChildModel((V1CamelScheduledPollConsumer) consumer);
        }
        _consume = consumer;
        return this;
    }

    @Override
    public GenericFileProducerBindingModel getProducer() {
        if (_produce == null) {
            Configuration config = getModelConfiguration().getFirstChild(
                    PRODUCE);
            _produce = new V1CamelFileProducerBindingModel(config,
                    getModelDescriptor());
        }
        return _produce;
    }

    @Override
    public V1CamelFileBindingModel setProducer(
            GenericFileProducerBindingModel producer) {
        Configuration config = getModelConfiguration().getFirstChild(PRODUCE);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(PRODUCE);
            getModelConfiguration().addChild(
                    ((V1GenericFileProducerBindingModel) producer)
                            .getModelConfiguration());
        } else {
            setChildModel((V1GenericFileProducerBindingModel) producer);
        }
        _produce = producer;
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        QueryString queryString = new QueryString();
        traverseConfiguration(children, queryString, DIRECTORY);

        URI newURI = URI.create("file://" + getDirectory() + queryString);
        return newURI;
    }

}
