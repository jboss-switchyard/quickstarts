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
package org.switchyard.component.jca.endpoint;

import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.jboss.logging.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.JCALogger;
import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.SwitchYardException;
import org.switchyard.selector.OperationSelector;

/**
 * Concrete message endpoint class for JCA message inflow using JMS MessageListener interface.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JMSEndpoint extends AbstractInflowEndpoint implements MessageListener {

    /** prefix for the context property. */
    public static final String CONTEXT_PROPERTY_PREFIX = "org.switchyard.component.jca.endpoint.";
    /** key for JNDI properties file. */
    public static final String KEY_JNDI_PROPERTIES_FILE = "jndiPropertiesFileName";
    /** key for JNDI properties file to look up the JMS destination. */
    public static final String KEY_DESTINATION_JNDI_PROPERTIES_FILE = "destinationJndiPropertiesFileName";
    /** key for ConnectionFactory JNDI name. */
    public static final String KEY_CONNECTION_FACTORY_JNDI_NAME = "connectionFactoryJndiName";
    /** key for replyTo/faultTo destination type. */
    public static final String KEY_DESTINATION_TYPE = "destinationType";
    /** key for replyTo destination name. */
    public static final String KEY_REPLY_TO = "replyTo";
    /** key for faultTo destination name. */
    public static final String KEY_FAULT_TO = "faultTo";
    /** key for output message type. */
    public static final String KEY_MESSAGE_TYPE = "messageType";
    /** key for user name. */
    public static final String KEY_USERNAME = "userName";
    /** key for password. */
    public static final String KEY_PASSWORD = "password";

    private Logger _logger = Logger.getLogger(JMSEndpoint.class);
    private MessageComposer<JMSBindingData> _composer;
    private OperationSelector<JMSBindingData> _selector;
    private String _jndiPropertiesFileName;
    private String _destinationJndiPropertiesFileName;
    private String _connectionFactoryJNDIName;
    private DestinationType _defaultDestinationType = DestinationType.JNDI;
    private String _defaultFaultTo;
    private String _defaultReplyTo;

    private String _userName;
    private String _password;
    private Properties _jndiProperties;
    private Properties _destinationJndiProperties;
    private ConnectionFactory _connectionFactory;
    private Destination _defaultFaultToJMSDestination;
    private Destination _defaultReplyToJMSDestination;
    private MessageType _defaultOutMessageType = MessageType.Object;

    private enum DestinationType {
        Queue, Topic, JNDI
    }

    private enum MessageType {
        Stream, Map, Text, Object, Bytes, Plain 
    }

    @Override
    public void initialize() {
        super.initialize();
        _composer = getMessageComposer(JMSBindingData.class);
        _selector = getOperationSelector(JMSBindingData.class);

        try {
            InitialContext cfic = null;
            if (getJndiProperties() != null) {
                cfic = new InitialContext(getJndiProperties());
            } else {
                cfic = new InitialContext();
            }
            if (_connectionFactoryJNDIName != null) {
                _connectionFactory = (ConnectionFactory) cfic.lookup(_connectionFactoryJNDIName);
            }

            // caching replyTo/faultTo destination if its type is JNDI
            if (_defaultDestinationType == DestinationType.JNDI) {
                InitialContext destic = null;
                if (getDestinationJndiProperties() != null) {
                    cfic.close();
                    destic = new InitialContext(getDestinationJndiProperties());
                } else {
                    destic = cfic;
                }

                if (_defaultFaultTo != null) {
                    try {
                        _defaultFaultToJMSDestination = (Destination) destic.lookup(_defaultFaultTo);
                    } catch (Exception e) {
                        JCALogger.ROOT_LOGGER.destinationNotFound(_defaultFaultTo, "faultTo");
                    }
                }
                if (_defaultReplyTo != null) {
                    try {
                        _defaultReplyToJMSDestination = (Destination) destic.lookup(_defaultReplyTo);
                    } catch (Exception e) {
                        JCALogger.ROOT_LOGGER.destinationNotFound(_defaultReplyTo, "replyTo");
                    }
                }
                destic.close();
            }
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.failedToInitialize(this.getClass().getName(), e);
        }
        
        if (_logger.isDebugEnabled()) {
            StringBuilder msg = new StringBuilder()
                .append("Initialized with: {")
                .append("Connection Factory:").append(_connectionFactoryJNDIName)
                .append(", Destination Type:").append(_defaultDestinationType)
                .append(", replyTo Name:").append(_defaultReplyTo)
                .append(", faultTo Name:").append(_defaultReplyTo)
                .append(", User Name:").append(_userName)
                .append(", Output Message Type:").append(_defaultOutMessageType)
                .append(", JNDI Properties File:").append(_jndiPropertiesFileName)
                .append(", Destination JNDI Properties File:").append(_destinationJndiPropertiesFileName)
                .append("}");
            _logger.debug(msg.toString());
        }
    }

    @Override
    public void onMessage(Message message) {

        try {
            JMSBindingData bindingData = new JMSBindingData(message);
            final String operation = _selector != null ? _selector.selectOperation(bindingData).getLocalPart() : null;
            SynchronousInOutHandler replyHandler = new SynchronousInOutHandler();
            Exchange exchange = createExchange(operation, replyHandler);
            exchange.send(_composer.compose(bindingData, exchange));

            if (_connectionFactory == null) {
                return;
            }
            
            // Process replyTo and faultTo if ConnectionFactory is available 
            Context context = exchange.getContext();
            Connection connection = null;
            
            try {
                if (_userName != null) {
                    connection = _connectionFactory.createConnection(_userName, _password);
                } else {
                    connection = _connectionFactory.createConnection();
                }
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                
                Destination faultTo = getFaultToDestinationFromContext(session, context);
                Destination replyTo = getReplyToDestinationFromContext(session, context);
                if (faultTo != null && ExchangeState.FAULT.equals(exchange.getState())) {
                    if (exchange.getMessage() != null) {
                        sendJMSMessage(session, faultTo, exchange, getOutputMessageTypeFromContext(context));
                    }
                } else if (replyTo != null && ExchangePattern.IN_OUT.equals(exchange.getPattern())) {
                        exchange = replyHandler.waitForOut();
                        if (exchange.getMessage() != null) {
                            sendJMSMessage(session, replyTo, exchange, getOutputMessageTypeFromContext(context));
                        }
                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    protected void sendJMSMessage(Session session, Destination destination, Exchange exchange, MessageType type) {
        try {
            MessageProducer producer = session.createProducer(destination);

            Message msg;
            switch (type) {
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
        } catch (Exception e) {
            JCALogger.ROOT_LOGGER.failedToSendMessage(destination.toString(), e.getMessage());
            if (_logger.isDebugEnabled()) {
                _logger.debug(e);
            }
        }
    }

    protected Destination getReplyToDestinationFromContext(Session session, Context ctx) {
        Destination replyToDestination = null;
        
        String key = CONTEXT_PROPERTY_PREFIX + KEY_REPLY_TO;
        if (ctx.getProperty(key) != null) {
            String replyToName = ctx.getPropertyValue(key);
            DestinationType replyToType = getDestinationTypeFromContext(ctx);
            
            try {
                switch (replyToType) {
                case JNDI:
                    replyToDestination = lookupDestinationFromJNDI(replyToName);
                    break;
                case Queue:
                    replyToDestination = session.createQueue(replyToName);
                    break;
                case Topic:
                    replyToDestination = session.createTopic(replyToName);
                    break;
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("replyTo is set to '" + replyToName + "'");
                }
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.contextDestinationNotFound(replyToName, _defaultReplyTo);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(e);
                }
            }
        }
        
        // No valid replyTo is specified in a context property - use default
        if (replyToDestination == null) {
            try {
                switch (_defaultDestinationType) {
                case JNDI:
                    replyToDestination = _defaultReplyToJMSDestination;
                    break;
                case Queue:
                    replyToDestination = session.createQueue(_defaultReplyTo);
                    break;
                case Topic:
                    replyToDestination = session.createTopic(_defaultReplyTo);
                    break;
                }
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.destinationNotFound(_defaultReplyTo, "replyTo");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(e);
                }
            }
        }
        return replyToDestination;
    }

    protected Destination getFaultToDestinationFromContext(Session session, Context ctx) {
        Destination faultToDestination = null;
        
        String key = CONTEXT_PROPERTY_PREFIX + KEY_FAULT_TO;
        if (ctx.getProperty(key) != null) {
            String faultToName = ctx.getPropertyValue(key);
            DestinationType faultToType = getDestinationTypeFromContext(ctx);
            
            try {
                switch (faultToType) {
                case JNDI:
                    faultToDestination = lookupDestinationFromJNDI(faultToName);
                    break;
                case Queue:
                    faultToDestination = session.createQueue(faultToName);
                    break;
                case Topic:
                    faultToDestination = session.createTopic(faultToName);
                    break;
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("faultTo is set to '" + faultToName + "'");
                }
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.contextDestinationNotFound(faultToName, _defaultFaultTo);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(e);
                }
            }
        }
        
        // No valid faultTo is specified in a context property - use default
        if (faultToDestination == null) {
            try {
                switch (_defaultDestinationType) {
                case JNDI:
                    faultToDestination = _defaultFaultToJMSDestination;
                    break;
                case Queue:
                    faultToDestination = session.createQueue(_defaultFaultTo);
                    break;
                case Topic:
                    faultToDestination = session.createTopic(_defaultFaultTo);
                    break;
                }
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.destinationNotFound(_defaultFaultTo, "faultTo");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(e);
                }
            }
        }
        return faultToDestination;
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

    protected Destination lookupOrCreateDestination(Session session, DestinationType type, String destName) throws Exception {
        switch (type) {
        case JNDI:
            return lookupDestinationFromJNDI(destName);
        case Queue:
            return session.createQueue(destName);
        case Topic:
            return session.createTopic(destName);
        default:
            return null;
        }
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

    protected MessageType getOutputMessageTypeFromContext(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_MESSAGE_TYPE;
        if (ctx.getProperty(key) != null) {
            MessageType type = MessageType.valueOf(ctx.getPropertyValue(key).toString());
            if (_logger.isDebugEnabled()) {
                _logger.debug("Output message type is set to '" + type.toString() + "'");
            }
            return type;
        }
        return _defaultOutMessageType;
    }

    /**
     * set ConnectionFactory JNDI name.
     * 
     * @param name ConnectionFactory JNDI name
     */
    public void setConnectionFactoryJNDIName(String name) {
        _connectionFactoryJNDIName = name;
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
     * set replyTo destination name.
     * 
     * @param name replyTo destination name
     */
    public void setReplyTo(String name) {
        _defaultReplyTo = name;
    }

    /**
     * set faultTo destination name.
     * 
     * @param name faultTo destination name
     */
    public void setFaultTo(String name) {
        _defaultFaultTo = name;
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
     * set message type.
     * @param type message type
     */
    public void setMessageType(String type) {
        _defaultOutMessageType = MessageType.valueOf(type);
    }

    /**
     * set JNDI properties file name.
     * @param name file name
     */
    public void setJndiPropertiesFileName(String name) {
            _jndiPropertiesFileName = name;
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

