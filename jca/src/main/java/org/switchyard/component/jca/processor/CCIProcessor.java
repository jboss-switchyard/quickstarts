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

import java.util.Properties;

import javax.naming.InitialContext;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.RecordFactory;

import org.jboss.logging.Logger;
import org.jboss.util.propertyeditor.PropertyEditors;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.jca.JCALogger;
import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.processor.cci.RecordHandler;
import org.switchyard.component.jca.processor.cci.RecordHandlerFactory;

/**
 * A concrete outbound processor class for CCI.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class CCIProcessor extends AbstractOutboundProcessor {
    /** default record type. */
    public static final String DEFAULT_RECORD_TYPE = "javax.resource.cci.MappedRecord";

    private Logger _logger = Logger.getLogger(CCIProcessor.class);
    private String _recordClassName;
    private ConnectionSpec _connectionSpec;
    private InteractionSpec _interactionSpec;
    private ConnectionFactory _connectionFactory;
    private RecordHandler<?> _recordHandler;
    
    @Override
    public AbstractOutboundProcessor setConnectionSpec(String name, Properties props) {
        try {
            Class<?> clazz = getApplicationClassLoader().loadClass(name);
            _connectionSpec = (ConnectionSpec) clazz.newInstance();
            if (!props.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(_connectionSpec, props);
            }
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.couldNotInitializeConnectionSpec(e);
        }
        
        return this;
    }

    @Override
    public AbstractOutboundProcessor setInteractionSpec(String name, Properties props) {
        try {
            Class<?> clazz = getApplicationClassLoader().loadClass(name);
            _interactionSpec = (InteractionSpec) clazz.newInstance();

            if (!props.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(_interactionSpec, props);
            }
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.couldNotInitializeInteractionSpec(e);
        }

        return this;
    }

    @Override
    public void initialize() {
        try {
            Class<?> clazz = getApplicationClassLoader().loadClass(_recordClassName);
            _recordHandler = RecordHandlerFactory.createRecordHandler(clazz, getApplicationClassLoader())
                    .setJCABindingModel(getJCABindingModel())
                    .setInteractionSpec(_interactionSpec);

            InitialContext ic = new InitialContext();
            _connectionFactory = (ConnectionFactory) ic.lookup(getConnectionFactoryJNDIName());
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.failedToInitialize(this.getClass().getName(), e);
        }
        try {
            RecordFactory factory = _connectionFactory.getRecordFactory();
            _recordHandler.setRecordFactory(factory);
        } catch (ResourceException e) {
            JCALogger.ROOT_LOGGER.failedToGetRecordFactory(e.getMessage());
        }
    }

    @Override
    public void uninitialize() {
        _connectionFactory = null;
    }

    @Override
    public Message process(Exchange exchange) throws HandlerException {
        Connection connection = null;
        Interaction interaction = null;
        try {
            if (_connectionSpec != null) {
                connection = _connectionFactory.getConnection(_connectionSpec);
            } else {
                connection = _connectionFactory.getConnection();
            }
            interaction = connection.createInteraction();
            return _recordHandler.handle(exchange, connection, interaction);
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.failedToProcessCCIOutboundInteraction(e);
        } finally {
            try {
                if (interaction != null) {
                    interaction.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (ResourceException e) {
                JCALogger.ROOT_LOGGER.failedToCloseInteractionConnection(e.getMessage());
                if (_logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * set {@link RecordFactory} implementation class name.
     * 
     * @param name class name
     */
    public void setRecordClassName(String name) {
        _recordClassName = name;
    }
}
