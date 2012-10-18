/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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

import org.apache.log4j.Logger;
import org.jboss.util.propertyeditor.PropertyEditors;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.jca.processor.cci.RecordHandler;
import org.switchyard.component.jca.processor.cci.RecordHandlerFactory;
import org.switchyard.exception.SwitchYardException;

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
    private RecordFactory _recordFactory;
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
            throw new IllegalArgumentException("Could not initialize ConnectionSpec", e);
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
            throw new IllegalArgumentException("Could not initialize InteractionSpec", e);
        }

        return this;
    }

    @Override
    public void initialize() {
        try {
            Class<?> clazz = getApplicationClassLoader().loadClass(_recordClassName);
            _recordHandler = RecordHandlerFactory.createRecordHandler(clazz, getApplicationClassLoader());

            InitialContext ic = new InitialContext();
            _connectionFactory = (ConnectionFactory) ic.lookup(getConnectionFactoryJNDIName());
            _recordFactory = _connectionFactory.getRecordFactory();
        } catch (Exception e) {
            throw new SwitchYardException("Failed to initialize " + this.getClass().getName(), e);
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
            connection = _connectionFactory.getConnection(_connectionSpec);
            interaction = connection.createInteraction();
            return _recordHandler.handle(exchange, _recordFactory, _interactionSpec, connection, interaction);
        } catch (Exception e) {
            throw new HandlerException("Failed to process CCI outbound interaction", e);
        } finally {
            try {
                if (interaction != null) {
                    interaction.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (ResourceException e) {
                _logger.warn("Failed to close Interaction/Connection: " + e.getMessage());
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
