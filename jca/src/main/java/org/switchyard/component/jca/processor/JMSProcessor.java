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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.JCALogger;
import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.composer.JMSBindingData;

/**
 * A concrete outbound processor class for JMS.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JMSProcessor extends AbstractOutboundProcessor {
    /** prefix for the context property. */
    public static final String CONTEXT_PROPERTY_PREFIX = "org.switchyard.component.jca.processor.";
    /** key for username property. */
    public static final String KEY_USERNAME = "userName";
    /** key for password property. */
    public static final String KEY_PASSWORD = "password";
    /** key for transacted property. */
    public static final String KEY_TRANSACTED = "transacted";
    /** key for acknowledge mode property. */
    public static final String KEY_ACKNOWLEDGE_MODE = "acknowledgeMode";
    /** key for destination property. */
    public static final String KEY_DESTINATION = "destination";
    /** key for message type property. */
    public static final String KEY_MESSAGE_TYPE  = "messageType";
    /** key for JNDI properties file to look up the JMS destination. */
    public static final String KEY_DESTINATION_JNDI_PROPERTIES_FILE = "destinationJndiPropertiesFileName";

    private Logger _logger = Logger.getLogger(JMSProcessor.class);
    private String _userName;
    private String _password;
    private String _transacted;
    private boolean _defaultTxEnabled;
    private String _destination;
    private String _acknowledgeMode;
    private int _defaultAckMode;
    private ConnectionFactory _connectionFactory;
    private Destination _defaultJmsDestination;
    private MessageComposer<JMSBindingData> _composer;
    private MessageType _defaultOutMessageType = MessageType.Object;
    private String _destinationJndiPropertiesFileName;
    private Properties _destinationJndiProperties;
    
    private enum MessageType {
        Stream, Map, Text, Object, Bytes, Plain 
    }
    
    @Override
    public AbstractOutboundProcessor setConnectionSpec(String name, Properties props) {
        // JMSProcessor doesn't need ConnectionSpec properties
        return this;
    }

    @Override
    public AbstractOutboundProcessor setInteractionSpec(String name, Properties props) {
        // JMSProcessor doesn't need InteractionSpec properties
        return this;
    }

    @Override
    public void initialize() {
        _defaultTxEnabled = Boolean.parseBoolean(_transacted);

        if (_acknowledgeMode == null || _acknowledgeMode.equals("")) {
            _defaultAckMode = Session.AUTO_ACKNOWLEDGE;
        } else {
            _defaultAckMode = Integer.parseInt(_acknowledgeMode);
        }

        if (_destination == null) {
            throw JCAMessages.MESSAGES.destinationPropertyMustBeSpecifiedInProcessorProperties();
        }
        
        _composer = getMessageComposer(JMSBindingData.class);
        
        try {
            InitialContext cfic = null;
            if (getJndiProperties() != null) {
                cfic = new InitialContext(getJndiProperties());
            } else {
                cfic = new InitialContext();
            }
            _connectionFactory = (ConnectionFactory) cfic.lookup(getConnectionFactoryJNDIName());
            
            InitialContext destic = null;
            if (getDestinationJndiProperties() != null) {
                cfic.close();
                destic = new InitialContext(getDestinationJndiProperties());
            } else {
                destic = cfic;
            }
            _defaultJmsDestination = (Destination) destic.lookup(_destination);
            destic.close();
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.failedToInitialize(this.getClass().getName(), e);
        }
    }

    @Override
    public void uninitialize() {
        _connectionFactory = null;
    }

    @Override
    public org.switchyard.Message process(Exchange exchange) throws HandlerException {
        Connection connection = null;
        Session session = null;
        try {
            if (_userName != null) {
                connection = _connectionFactory.createConnection(_userName, _password);
            } else {
                connection = _connectionFactory.createConnection();
            }
            connection.start();
            
            Context context = exchange.getContext();
            session = connection.createSession(getTxEnabledFromContext(context), getAcknowledgeModeFromContext(context));
            MessageProducer producer = session.createProducer(getDestinationFromContext(context));
            
            Message msg;
            switch (getOutputMessageTypeFromContext(context)) {
            case Stream:
                msg = session.createStreamMessage();
                break;
            case Map:
                msg = session.createMapMessage();
                break;
            case Text:
                msg = session.createTextMessage();
                break;
            case Bytes:
                msg = session.createBytesMessage();
                break;
            case Plain:
                msg = session.createMessage();
                break;
            default:
                    msg = session.createObjectMessage();
            }
            
            producer.send(_composer.decompose(exchange, new JMSBindingData(msg)).getMessage());
            
            if (session.getTransacted()) {
                try {
                    session.commit();
                } catch (Exception e) {
                    // managed by global transaction. ignoring...
                    if (_logger.isDebugEnabled()) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            try {
                if (session != null && session.getTransacted()) {
                    session.rollback();
                }
            } catch (Exception e2) {
                // managed by global transaction. ignoring...
                if (_logger.isDebugEnabled()) {
                    e2.printStackTrace();
                }
            }
            throw JCAMessages.MESSAGES.failedToProcessJMSOutboundInteraction(e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                JCALogger.ROOT_LOGGER.failedToCloseJMSSessionconnection(e.getMessage());
                if (_logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected boolean getTxEnabledFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_TRANSACTED;
        if (ctx.getProperty(key) != null) {
            return Boolean.parseBoolean(ctx.getProperty(key).getValue().toString());
        }
        return _defaultTxEnabled;
    }
    
    protected int getAcknowledgeModeFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_ACKNOWLEDGE_MODE;
        if (ctx.getProperty(key) != null) {
            return Integer.parseInt(ctx.getProperty(key).getValue().toString());
        }
        return _defaultAckMode;
    }

    protected Destination getDestinationFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_DESTINATION;
        if (ctx.getProperty(key) != null) {
            String destName = ctx.getProperty(key).getValue().toString();
            InitialContext ic = null;
            try {
                if (getDestinationJndiProperties() != null) {
                    ic = new InitialContext(getDestinationJndiProperties());
                } else if (getJndiProperties() != null) {
                    ic = new InitialContext(getJndiProperties());
                } else {
                    ic = new InitialContext();
                }
                return (Destination) ic.lookup(destName);
            } catch (NamingException e) {
                JCALogger.ROOT_LOGGER.contextDestinationNotFound(destName, _destination);
            } finally {
                if (ic != null) {
                    try {
                        ic.close();
                    } catch (Exception e) {
                        // avoid checkstype error
                        e.getMessage();
                        }
                }
            }
        }
        return _defaultJmsDestination;
        
    }
    
    protected MessageType getOutputMessageTypeFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_MESSAGE_TYPE;
        if (ctx.getProperty(key) != null) {
            return MessageType.valueOf(ctx.getProperty(key).getValue().toString());
        }
        return _defaultOutMessageType;
    }

    /**
     * set destination name.
     * @param name destination name
     */
    public void setDestination(String name) {
        _destination = name;
    }

    /**
     * set user name.
     * 
     * @param user user name
     */
    public void setUsername(String user) {
        _userName = user;
    }
    
    /**
     * set password.
     * 
     * @param passwd password
     */
    public void setPassword(String passwd) {
        _password = passwd;
    }
    
    /**
     * set transacted.
     * 
     * @param tx "false" when disable transaction
     */
    public void setTransacted(String tx) {
        _transacted = tx;
    }
    
    /**
     * acknowledge mode.
     * 
     * @param ack integer value of acknowledge mode.
     * @see javax.jms.Session
     */
    public void setAcknowledgeMode(String ack) {
        _acknowledgeMode = ack;
    }
    
    /**
     * set message type.
     * @param type message type
     */
    public void setMessageType(String type) {
        _defaultOutMessageType = MessageType.valueOf(type);
    }
    
    /**
     * set JNDI properties file name for destination lookup.
     * @param name filename
     */
    public void setDestinationJndiPropertiesFileName(String name) {
        _destinationJndiPropertiesFileName = name;
    }
    
    /**
     * get JNDI properties for destination lookup.
     * @return InitialContext properties for destination lookup
     */
    public Properties getDestinationJndiProperties() {
        if (_destinationJndiPropertiesFileName != null && _destinationJndiProperties == null) {
            try {
                InputStream is = Classes.getResourceAsStream(_destinationJndiPropertiesFileName);
                Properties props = new Properties();
                props.load(is);
                is.close();
                _destinationJndiProperties = props;
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.failedToLoadJndiPropertiesFile(_destinationJndiPropertiesFileName, e);
            }
        }
        return _destinationJndiProperties;
    }
}
