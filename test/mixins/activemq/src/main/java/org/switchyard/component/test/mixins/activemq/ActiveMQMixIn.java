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
package org.switchyard.component.test.mixins.activemq;

import java.io.File;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.switchyard.SwitchYardException;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * ActiveMQ Test Mix In.
 */
public class ActiveMQMixIn extends AbstractTestMixIn {

    private String _config;
    private String _url = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;
    private String _user = ActiveMQConnectionFactory.DEFAULT_USER;
    private String _passwd = ActiveMQConnectionFactory.DEFAULT_PASSWORD; 

    /**
     * Embedded broker.
     */
    private BrokerService _broker;

    private Connection _connection;

    /**
     * Default constructor.
     */
    public ActiveMQMixIn() {
        this(null);
    }

    /**
     * Constructor.
     * @param config Name of ActiveMQ configuration file (spring xml context).
     */
    public ActiveMQMixIn(String config) {
        _config = config;
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            if (_config == null) {
                _broker = new BrokerService();
                _broker.setStartAsync(true);
                _broker.setBrokerName("default");
                _broker.setUseJmx(false);
                _broker.setPersistent(false);
                _broker.setDataDirectoryFile(new File(System.getProperty("java.io.tmpdir"), "activemq-data"));
                _broker.addConnector(ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);
                _broker.setUseShutdownHook(false);
            } else {
                _broker = BrokerFactory.createBroker(_config);
            }

            _broker.start();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set user to connect.
     * @param user user
     * @return this instance
     */
    public ActiveMQMixIn setUser(String user) {
        _user = user;
        return this;
    }

    /**
     * Set password to connect.
     * @param passwd password
     * @return this instance
     */
    public ActiveMQMixIn setPassword(String passwd) {
        _passwd = passwd;
        return this;
    }

    /**
     * Set configuration file.
     * @param config configuration
     * @return this instance
     */
    public ActiveMQMixIn setConfig(String config) {
        _config = config;
        return this;
    }

    @Override
    public void uninitialize() {
        try {
            if (_connection != null) {
                _connection.close();
            }
            if (_broker != null) {
                _broker.stop();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            // always clean JNDI context
            super.uninitialize();
        }
    }
    
    /**
     * Get a JMS Session from an ActiveMQ connection.
     * @return JMS Session
     */
    public Session getSession() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(_url);
        try {
            if (_connection == null) {
                _connection = connectionFactory.createConnection(_user, _passwd);
                _connection.start();
            }
            return _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new SwitchYardException(e);
        }
    }

}
