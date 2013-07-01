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
package org.switchyard.component.camel.file.model.v1;

import static org.switchyard.component.camel.file.model.Constants.FILE_NAMESPACE_V1;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.file.GenericFileProducerBindingModel;
import org.switchyard.component.camel.common.model.file.v1.V1GenericFileBindingModel;
import org.switchyard.component.camel.common.model.file.v1.V1GenericFileProducerBindingModel;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.component.camel.file.model.CamelFileBindingModel;
import org.switchyard.component.camel.file.model.CamelFileConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
/**
 * A binding for Camel's file component.
 * 
 * @author Daniel Bevenius
 * 
 */
public class V1CamelFileBindingModel extends V1GenericFileBindingModel
    implements CamelFileBindingModel {

    /**
     * The name of this binding type ("binding.file").
     */
    public static final String FILE = "file";

    /**
     * In charge of parsing out consumer options
     */
    private CamelFileConsumerBindingModel _consume;

    /**
     * In charge of parsing out producer options
     */
    private GenericFileProducerBindingModel _produce;

    /**
     * Create a new V1CamelFileBindingModel.
     */
    public V1CamelFileBindingModel() {
        super(FILE, FILE_NAMESPACE_V1);

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
    public V1CamelFileBindingModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    @Override
    public CamelFileConsumerBindingModel getConsumer() {
        if (_consume == null) {
            Configuration config = getModelConfiguration().getFirstChild(CONSUME);
            _consume = new V1CamelFileConsumerBindingModel(config, getModelDescriptor());
        }
        return _consume;
    }

    @Override
    public V1CamelFileBindingModel setConsumer(CamelFileConsumerBindingModel consumer) {
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
            Configuration config = getModelConfiguration().getFirstChild(PRODUCE);
            _produce = new V1CamelFileProducerBindingModel(config, getModelDescriptor());
        }
        return _produce;
    }

    @Override
    public V1CamelFileBindingModel setProducer(GenericFileProducerBindingModel producer) {
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

        URI newURI = URI.create(FILE + "://" + getDirectory() + queryString);
        return newURI;
    }

}
