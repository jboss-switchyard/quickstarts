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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.SwitchYardException;

/**
 * A concrete outbound processor class for JMS.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JMSProcessor extends AbstractOutboundProcessor {
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
            
    private Logger _logger = Logger.getLogger(JMSProcessor.class);
    private String _userName;
    private String _password;
    private String _transacted;
    private boolean _txEnabled;
    private String _destination;
    private String _acknowledgeMode;
    private int _ackMode;
    private ConnectionFactory _connectionFactory;
    private Destination _jmsDestination;
    private MessageComposer<JMSBindingData> _composer;
    private MessageType _outMessageType = MessageType.Object;
    
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
        _txEnabled = Boolean.parseBoolean(_transacted);

        if (_acknowledgeMode == null || _acknowledgeMode.equals("")) {
            _ackMode = Session.AUTO_ACKNOWLEDGE;
        } else {
            _ackMode = Integer.parseInt(_acknowledgeMode);
        }

        if (_destination == null) {
            throw new SwitchYardException("destination property must be specified in Processor properties");
        }
        
        _composer = getMessageComposer(JMSBindingData.class);
        
        try {
            InitialContext ic = new InitialContext();
            _connectionFactory = (ConnectionFactory) ic.lookup(getConnectionFactoryJNDIName());
            _jmsDestination = (Destination) ic.lookup(_destination);
        } catch (Exception e) {
            throw new SwitchYardException("Failed to initialize " + this.getClass().getName(), e);
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
            
            session = connection.createSession(_txEnabled, _ackMode);
            MessageProducer producer = session.createProducer(_jmsDestination);
            
            Message msg;
            switch (_outMessageType) {
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
            return null;
        } catch (Exception e) {
            throw new HandlerException("Failed to process JMS outbound interaction", e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                _logger.warn("Failed to close JMS session/connection: " + e.getMessage());
                if (_logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }
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
        _outMessageType = MessageType.valueOf(type);
    }
}
