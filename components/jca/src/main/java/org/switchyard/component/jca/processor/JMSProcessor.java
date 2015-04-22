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
    /** key for destination type property. */
    public static final String KEY_DESTINATION_TYPE = "destinationType";
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
    private DestinationType _defaultDestinationType = DestinationType.JNDI;
    private String _defaultDestination;
    private String _acknowledgeMode;
    private int _defaultAckMode;
    private ConnectionFactory _connectionFactory;
    private Destination _defaultJmsDestination;
    private MessageComposer<JMSBindingData> _composer;
    private MessageType _defaultOutMessageType = MessageType.Object;
    private String _destinationJndiPropertiesFileName;
    private Properties _destinationJndiProperties;
    
    private enum DestinationType {
        Queue, Topic, JNDI
    }
    
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

        if (_defaultDestination == null) {
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
            
            // caching destination if its type is JNDI
            if (_defaultDestinationType == DestinationType.JNDI) {
                InitialContext destic = null;
                if (getDestinationJndiProperties() != null) {
                    cfic.close();
                    destic = new InitialContext(getDestinationJndiProperties());
                } else {
                    destic = cfic;
                }
                _defaultJmsDestination = (Destination) destic.lookup(_defaultDestination);
                destic.close();
            }
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.failedToInitialize(this.getClass().getName(), e);
        }
        
        if (_logger.isDebugEnabled()) {
            StringBuilder msg = new StringBuilder()
                .append("Initialized with: {")
                .append("Connection Factory:").append(getConnectionFactoryJNDIName())
                .append(", Destination Type:").append(_defaultDestinationType)
                .append(", Destination Name:").append(_defaultDestination)
                .append(", User Name:").append(_userName)
                .append(", Output Message Type:").append(_defaultOutMessageType)
                .append(", JNDI Properties File:").append(getJndiPropertiesFileName())
                .append(", Destination JNDI Properties File:").append(_destinationJndiPropertiesFileName)
                .append("}");
            _logger.debug(msg.toString());
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
            MessageProducer producer = session.createProducer(getDestinationFromContext(session, context));
            
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
            boolean transacted = Boolean.parseBoolean(ctx.getPropertyValue(key).toString());
            if (_logger.isDebugEnabled()) {
                _logger.debug("transacted is set to '" + transacted + "'");
            }
            return transacted;
        }
        return _defaultTxEnabled;
    }
    
    protected int getAcknowledgeModeFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_ACKNOWLEDGE_MODE;
        if (ctx.getProperty(key) != null) {
            int ackMode = Integer.parseInt(ctx.getPropertyValue(key).toString());
            if (_logger.isDebugEnabled()) {
                _logger.debug("Acknowledge Mode is set to '" + ackMode + "'");
            }
            return ackMode;
        }
        return _defaultAckMode;
    }

    protected Destination getDestinationFromContext(Session session, Context ctx) throws Exception {
        Destination destination = null;

        String key = CONTEXT_PROPERTY_PREFIX + KEY_DESTINATION;
        if (ctx.getProperty(key) != null) {
            String destName = ctx.getPropertyValue(key);
            DestinationType destType = getDestinationTypeFromContext(ctx);
            
            try {
                switch (destType) {
                case JNDI:
                    destination = lookupDestinationFromJNDI(destName);
                    break;
                case Queue:
                    destination = session.createQueue(destName);
                    break;
                case Topic:
                    destination = session.createTopic(destName);
                    break;
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Destination is set to '" + destName + "'");
                }
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.contextDestinationNotFound(destName, _defaultDestination);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(e);
                }
            }
        }
        
        // No valid destination is specified in a context property - use default
        if (destination == null) {
            switch (_defaultDestinationType) {
            case JNDI:
                destination = _defaultJmsDestination;
                break;
            case Queue:
                destination = session.createQueue(_defaultDestination);
                break;
            case Topic:
                destination = session.createTopic(_defaultDestination);
                break;
            }
        }
        return destination;
    }

    protected Destination lookupDestinationFromJNDI(String destName) throws Exception {
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
        } finally {
            if (ic != null) {
                try {
                    ic.close();
                } catch (Exception e) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(e);
                    }
                }
            }
        }
    }

    protected DestinationType getDestinationTypeFromContext(Context ctx) {
        DestinationType destType = _defaultDestinationType;
        String key = CONTEXT_PROPERTY_PREFIX + KEY_DESTINATION_TYPE;
        if (ctx.getProperty(key) != null) {
            String type = ctx.getPropertyValue(key);
            DestinationType ctxType = parseDestinationType(type);
            if (ctxType != null) {
                destType = ctxType;
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Destination type is set to '" + destType.toString() + "'");
                }
            }
        }
        return destType;
    }

    protected MessageType getOutputMessageTypeFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_MESSAGE_TYPE;
        if (ctx.getProperty(key) != null) {
            MessageType msgType = MessageType.valueOf(ctx.getPropertyValue(key).toString());
            if (_logger.isDebugEnabled()) {
                _logger.debug("Output message type is set to '" + msgType.toString() + "'");
            }
            return msgType;
        }
        return _defaultOutMessageType;
    }

    /**
     * Set destination type.
     * @param type destination type
     */
    public void setDestinationType(String type) {
        DestinationType destType = parseDestinationType(type);
        if (destType == null) {
            JCALogger.ROOT_LOGGER.invalidDestinationType(type, DestinationType.JNDI.toString());
            destType = DestinationType.JNDI;
        }
        _defaultDestinationType = destType;
    }

    protected DestinationType parseDestinationType(String type) {
        DestinationType destType = null;
        if (type.equals(javax.jms.Queue.class.getName()) || type.equalsIgnoreCase("queue")) {
            destType = DestinationType.Queue;
        } else if (type.equals(javax.jms.Topic.class.getName()) || type.equalsIgnoreCase("Topic")) {
            destType = DestinationType.Topic;
        } else if (type.equalsIgnoreCase("JNDI")) {
            destType = DestinationType.JNDI;
        }
        return destType;
    }

    /**
     * set destination name.
     * @param name destination name
     */
    public void setDestination(String name) {
        _defaultDestination = name;
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
