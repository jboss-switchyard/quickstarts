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

    private MessageComposer<JMSBindingData> _composer;
    private OperationSelector<JMSBindingData> _selector;
    private String _jndiPropertiesFileName;
    private String _destinationJndiPropertiesFileName;
    private String _connectionFactoryJNDIName;
    private String _defaultFaultTo;
    private String _defaultReplyTo;

    private String _userName;
    private String _password;
    private Properties _jndiProperties;
    private Properties _destinationJndiProperties;
    private ConnectionFactory _connectionFactory;
    private Destination _faultToJMSDestination;
    private Destination _replyToJMSDestination;
    private MessageType _defaultOutMessageType = MessageType.Object;

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

            InitialContext destic = null;
            if (getDestinationJndiProperties() != null) {
                cfic.close();
                destic = new InitialContext(getDestinationJndiProperties());
            } else {
                destic = cfic;
            }

            if (_defaultFaultTo != null) {
                try {
                    _faultToJMSDestination = (Destination) destic.lookup(_defaultFaultTo);
                } catch (Exception e) {
                    JCALogger.ROOT_LOGGER.destinationNotFound(_defaultFaultTo, "faultTo");
                }
            }
            if (_defaultReplyTo != null) {
                try {
                    _replyToJMSDestination = (Destination) destic.lookup(_defaultReplyTo);
                } catch (Exception e) {
                    JCALogger.ROOT_LOGGER.destinationNotFound(_defaultReplyTo, "replyTo");
                }
            }
            destic.close();
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.failedToInitialize(this.getClass().getName(), e);
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

            Context context = exchange.getContext();
            Destination faultTo = getFaultToDestination(context);
            Destination replyTo = getReplyToDestination(context);
            if (faultTo != null && ExchangeState.FAULT.equals(exchange.getState())) {
                if (exchange.getMessage() != null) {
                    sendJMSMessage(faultTo, exchange, getOutputMessageType(context));
                }
            } else if (replyTo != null && ExchangePattern.IN_OUT.equals(exchange.getPattern())) {
                    exchange = replyHandler.waitForOut();
                    if (exchange.getMessage() != null) {
                        sendJMSMessage(replyTo, exchange, getOutputMessageType(context));
                    }
            }
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    protected void sendJMSMessage(Destination destination, Exchange exchange, MessageType type) {
        Connection connection = null;
        try {
            if (_userName != null) {
                connection = _connectionFactory.createConnection(_userName, _password);
            } else {
                connection = _connectionFactory.createConnection();
            }
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    JCALogger.ROOT_LOGGER.failedToCloseJMSSessionconnection(e.getMessage());
                }
            }
        }
    }

    protected Destination getReplyToDestination(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_REPLY_TO;
        if (ctx.getProperty(key) != null) {
            String destName = ctx.getPropertyValue(key);
            try {
                return getDestinationFromContext(destName);
            } catch (Exception e) {
                if (_defaultReplyTo != null) {
                    JCALogger.ROOT_LOGGER.contextDestinationNotFound(destName, _defaultReplyTo);
                } else {
                    JCALogger.ROOT_LOGGER.destinationNotFound(destName, "replyTo");
                }
            }
        }
        return _replyToJMSDestination;
    }

    protected Destination getFaultToDestination(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_FAULT_TO;
        if (ctx.getProperty(key) != null) {
            String destName = ctx.getPropertyValue(key);
            try {
                return getDestinationFromContext(destName);
            } catch (Exception e) {
                if (_defaultFaultTo != null) {
                    JCALogger.ROOT_LOGGER.contextDestinationNotFound(destName, _defaultFaultTo);
                } else {
                    JCALogger.ROOT_LOGGER.destinationNotFound(destName, "faultTo");
                }
            }
        }
        return _faultToJMSDestination;
    }

    protected Destination getDestinationFromContext(String destName) throws Exception {
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
                    e.getMessage();
                }
            }
        }
    }

    protected MessageType getOutputMessageType(Context ctx) {
        String key = CONTEXT_PROPERTY_PREFIX + KEY_MESSAGE_TYPE;
        if (ctx.getProperty(key) != null) {
            return MessageType.valueOf(ctx.getPropertyValue(key).toString());
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
