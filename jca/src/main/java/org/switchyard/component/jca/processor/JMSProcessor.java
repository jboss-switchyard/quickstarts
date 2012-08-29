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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JCAComposition;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.exception.SwitchYardException;

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

    private String _userName;
    private String _password;
    private String _transacted;
    private boolean _txEnabled;
    private String _destination;
    private String _acknowledgeMode;
    private int _ackMode;
    private ConnectionFactory _connectionFactory;
    private Destination _jmsDestination;
    
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
        _destination = null;
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

            Message msg = session.createObjectMessage();
            MessageComposer<JMSBindingData> composer = JCAComposition.getMessageComposer(JMSBindingData.class);
            producer.send(composer.decompose(exchange, new JMSBindingData(msg)).getMessage());
            return null;
        } catch (Exception e) {
            throw new HandlerException("Failed to process JMS outbound interaction", e);
        } finally {
            try {
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.getMessage(); // ignore
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
    
}
