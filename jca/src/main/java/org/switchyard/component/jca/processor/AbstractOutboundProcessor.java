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
package org.switchyard.component.jca.processor;

import java.io.InputStream;
import java.util.Properties;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.JCALogger;
import org.switchyard.component.jca.composer.JCABindingData;
import org.switchyard.component.jca.composer.JCAComposition;
import org.switchyard.component.jca.config.model.JCABindingModel;

/**
 * Abstract class for processing outbound delivery regarding to JCA outbound contract.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public abstract class AbstractOutboundProcessor {
    
    /** key for JNDI properties file. */
    public static final String KEY_JNDI_PROPERTIES_FILE = "jndiPropertiesFileName";

    private String _connectionFactoryJNDIName;
    private Properties _mcfProperties;
    private String _jndiPropertiesFileName;
    private Properties _jndiProperties;
    private ClassLoader _appClassLoader;
    private JCABindingModel _jcaBindingModel;
    
    /**
     * process outbound delivery.
     * 
     * @param exchange {@link Exchange}
     * @return response message
     * @throws HandlerException failed to deliver
     */
    public abstract Message process(Exchange exchange) throws HandlerException;

    /**
     * initialize the processor.
     */
    public abstract void initialize();
    
    /**
     * uninitialize the processor.
     */
    public abstract void uninitialize();
    
    /**
     * set connection information.
     * 
     * @param name name of the class which represents connection information
     * @param props connection properties
     * @return {@link AbstractOutboundProcessor} to support method chaining
     */
    public abstract AbstractOutboundProcessor setConnectionSpec(String name, Properties props);
    
    /**
     * set interaction information.
     * 
     * @param name name of the class which represents interaction information
     * @param props interaction properties
     * @return {@link AbstractOutboundProcessor} to support method chaining
     */
    public abstract AbstractOutboundProcessor setInteractionSpec(String name, Properties props);
    
    /**
     * set connection factory JNDI name.
     * 
     * @param name connection factory JNDI name
     * @return {@link AbstractOutboundProcessor} to support method chaining
     */
    public AbstractOutboundProcessor setConnectionFactoryJNDIName(String name) {
        _connectionFactoryJNDIName = name;
        return this;
    }
    
    /**
     * get connection factory JNDI name.
     * 
     * @return connection factory JNDI name
     */
    public String getConnectionFactoryJNDIName() {
        return _connectionFactoryJNDIName;
    }
    
    /**
     * set managed connection factory properties.
     * 
     * @param props {@link Properties} for managed connection factory
     * @return {@link AbstractOutboundProcessor} to support method chaining
     */
    public AbstractOutboundProcessor setMCFProperties(Properties props) {
        _mcfProperties = props;
        return this;
    }
    
    /**
     * get managed connection factory properties.
     * 
     * @return {@link Properties} for managed connection factory
     */
    public Properties getMCFProperties() {
        return _mcfProperties;
    }

    /**
     * set application class loader.
     * 
     * @param loader application class loader
     * @return {@link AbstractOutboundProcessor} to support method chaining
     */
    public AbstractOutboundProcessor setApplicationClassLoader(ClassLoader loader) {
        _appClassLoader = loader;
        return this;
    }
    
    /**
     * get application class loader.
     * 
     * @return application class loader
     */
    public ClassLoader getApplicationClassLoader() {
        return _appClassLoader;
    }
    
    /**
     * set JCA binding model.
     * @param model JCA binding model
     * @return {@link AbstractOutboundProcessor} to support method chaining
     */
    public AbstractOutboundProcessor setJCABindingModel(JCABindingModel model) {
        _jcaBindingModel = model;
        return this;
    }
    
    /**
     * get JCA binding model.
     * @return JCA binding model
     */
    public JCABindingModel getJCABindingModel() {
        return _jcaBindingModel;
    }
    
    protected <D extends JCABindingData> MessageComposer<D> getMessageComposer(Class<D> clazz) {
        return JCAComposition.getMessageComposer(_jcaBindingModel, clazz);
    }
    
    /**
     * set JNDI properties file name.
     * @param name file name
     */
    public void setJndiPropertiesFileName(String name) {
            _jndiPropertiesFileName = name;
    }
    
    /**
     * get JNDI properties file name.
     * @return file name
     */
    public String getJndiPropertiesFileName() {
        return _jndiPropertiesFileName;
    }
    
    /**
     * get JNDI properties.
     * @return JNDI properties
     */
    public Properties getJndiProperties() {
        if (_jndiPropertiesFileName != null && _jndiProperties == null) {
            try {
                InputStream is = Classes.getResourceAsStream(_jndiPropertiesFileName);
                Properties props = new Properties();
                props.load(is);
                is.close();
                _jndiProperties = props;
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.failedToLoadJndiPropertiesFile(_jndiPropertiesFileName, e);
            }
        }
        return _jndiProperties;
    }
}
