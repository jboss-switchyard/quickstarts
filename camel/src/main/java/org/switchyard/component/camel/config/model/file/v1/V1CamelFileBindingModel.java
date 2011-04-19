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
import java.util.Iterator;
import java.util.List;

import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.file.CamelFileBindingModel;
import org.switchyard.component.camel.config.model.file.CamelFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.file.CamelFileProducerBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A binding for Camel's file component.
 * 
 * @author Daniel Bevenius
 * 
 */
public class V1CamelFileBindingModel extends V1BaseCamelBindingModel implements
        CamelFileBindingModel {

    /**
     * The name of this binding type ("binding.file").
     */
    public static final String FILE = "file";

    /**
     * The name of the 'targetDir' element.
     */
    public static final String TARGET_DIR = "targetDir";

    /**
     * The name of the 'autoCreate' element.
     */
    public static final String AUTO_CREATE = "autoCreate";

    /**
     * The name of the 'bufferSize' element.
     */
    public static final String BUFFER_SIZE = "bufferSize";

    /**
     * The name of the 'fileName' element.
     */
    public static final String FILE_NAME = "fileName";

    /**
     * The name of the 'flatten' element.
     */
    public static final String FLATTEN = "flatten";

    /**
     * The name of the 'charset' element.
     */
    public static final String CHARSET = "charset";

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
    private CamelFileProducerBindingModel _produce;

    /**
     * Create a new V1CamelFileBindingModel.
     */
    public V1CamelFileBindingModel() {
        super(FILE);

        setModelChildrenOrder(OperationSelector.OPERATION_SELECTOR, TARGET_DIR,
                AUTO_CREATE, BUFFER_SIZE, FILE_NAME, FLATTEN, CHARSET, CONSUME,
                PRODUCE);
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

    /**
     * Returns the target directory for file endpoints.
     * 
     * @return file reference for the target path, or null if no TARGET_DIR
     *         config was specified.
     */
    @Override
    public String getTargetDir() {
        return getConfig(TARGET_DIR);
    }

    @Override
    public V1CamelFileBindingModel setTargetDir(String targetDir) {
        setConfig(TARGET_DIR, String.valueOf(targetDir));
        return this;
    }

    @Override
    public Boolean isAutoCreate() {
        return getBooleanConfig(AUTO_CREATE);
    }

    @Override
    public V1CamelFileBindingModel setAutoCreate(Boolean autoCreate) {
        setConfig(AUTO_CREATE, String.valueOf(autoCreate));
        return this;
    }

    @Override
    public Integer getBufferSize() {
        return getIntegerConfig(BUFFER_SIZE);
    }

    @Override
    public V1CamelFileBindingModel setBufferSize(Integer bufferSize) {
        setConfig(BUFFER_SIZE, String.valueOf(bufferSize));
        return this;
    }

    @Override
    public String getFileName() {
        return getConfig(FILE_NAME);
    }

    @Override
    public V1CamelFileBindingModel setFileName(String fileName) {
        setConfig(FILE_NAME, fileName);
        return this;
    }

    @Override
    public Boolean isFlatten() {
        return getBooleanConfig(FLATTEN);
    }

    @Override
    public V1CamelFileBindingModel setFlatten(Boolean flatten) {
        setConfig(FLATTEN, String.valueOf(flatten));
        return this;
    }

    @Override
    public String getCharset() {
        return getConfig(CHARSET);
    }

    @Override
    public V1CamelFileBindingModel setCharset(String charset) {
        setConfig(CHARSET, charset);
        return this;
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
                    ((V1CamelFileConsumerBindingModel) consumer)
                            .getModelConfiguration());
        } else {
            setChildModel((V1CamelFileConsumerBindingModel) consumer);
        }
        _consume = consumer;
        return this;
    }

    @Override
    public CamelFileProducerBindingModel getProducer() {
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
            CamelFileProducerBindingModel producer) {
        Configuration config = getModelConfiguration().getFirstChild(PRODUCE);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(PRODUCE);
            getModelConfiguration().addChild(
                    ((V1CamelFileProducerBindingModel) producer)
                            .getModelConfiguration());
        } else {
            setChildModel((V1CamelFileProducerBindingModel) producer);
        }
        _produce = producer;
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        QueryString queryString = new QueryString();
        traverseConfiguration(children, queryString);

        URI newURI = URI.create("file://" + getTargetDir() + queryString);
        return newURI;
    }

    private void traverseConfiguration(List<Configuration> parent,
            QueryString queryString) {

        if (parent.size() != 0) {
            Iterator<Configuration> parentIterator = parent.iterator();
            while (parentIterator.hasNext()) {
                Configuration child = parentIterator.next();

                if (child != null
                        && child.getName() != null
                        && (child.getName().equalsIgnoreCase(TARGET_DIR) || child
                                .getName().equalsIgnoreCase(
                                        OperationSelector.OPERATION_SELECTOR))) {
                    continue;
                }

                if (child != null && child.getChildren().size() == 0) {
                    queryString.add(child.getName(), child.getValue());
                } else {
                    traverseConfiguration(child.getChildren(), queryString);
                }
            }
        }
    }

    private Integer getIntegerConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Integer.parseInt(value) : null;
    }

    private Boolean getBooleanConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Boolean.valueOf(value) : null;
    }

    private String getConfig(String configName) {
        Configuration config = getModelConfiguration()
                .getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }

    private void setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
    }
}
