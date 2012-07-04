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
package org.switchyard.test.mixins;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.registry.JndiBindingRegistry;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.switchyard.test.MixInDependencies;

/**
 * HornetQ Test Mix In.
 * 
 * @author Daniel Bevenius
 */
@MixInDependencies(required={NamingMixIn.class})
public class HornetQMixIn extends AbstractTestMixIn {
    
    private static final String HORNETQ_CONF_FILE = "hornetq-configuration.xml";
    private static final String HORNETQ_JMS_CONF_FILE = "hornetq-jms.xml";

    private Logger _logger = Logger.getLogger(HornetQMixIn.class);
    private boolean _startEmbedded;
    private String _host = TransportConstants.DEFAULT_HOST;
    private int _port = TransportConstants.DEFAULT_PORT;
    private String _user = null;
    private String _passwd = null; 
    private EmbeddedJMS _embeddedJMS;
    private ServerLocator _serverLocator;
    private ClientSessionFactory _clientSessionFactory;
    private ClientSession _clientSession;

    private HornetQConnectionFactory _jmsConnectionFactory;
    private Connection _jmsConnection;
    private Session _jmsSession;

    /**
     * Default constructor.
     */
    public HornetQMixIn() {
        this(true);
    }
    
    /**
     * Constructor.
     * @param embedded true if you want to start embedded HornetQ server,
     * or false if you want to connect to remote HornetQ server.
     */
    public HornetQMixIn(boolean embedded) {
        _startEmbedded = embedded;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (_startEmbedded) {
            _embeddedJMS = new EmbeddedJMS();
            _embeddedJMS.setConfigResourcePath(HORNETQ_CONF_FILE);
            _embeddedJMS.setJmsConfigResourcePath(HORNETQ_JMS_CONF_FILE);
            try {
                _embeddedJMS.setRegistry(new JndiBindingRegistry());
                _embeddedJMS.start();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Set host address to connect.
     * @param host address
     * @return this instance
     */
    public HornetQMixIn setHost(String host) {
        _host = host;
        return this;
    }
    
    /**
     * Set port number to connect.
     * @param port port number
     * @return this instance
     */
    public HornetQMixIn setPort(int port) {
        _port = port;
        return this;
    }
    
    /**
     * Set user to connect.
     * @param user user
     * @return this instance
     */
    public HornetQMixIn setUser(String user) {
        _user = user;
        return this;
    }
    
    /**
     * Set password to connect.
     * @param passwd password
     * @return this instance
     */
    public HornetQMixIn setPassword(String passwd) {
        _passwd = passwd;
        return this;
    }
    
    /**
     * Gets the HornetQ {@link Configuration}.
     * 
     * @return Configuration the HornetQ configuration used for this Embedded Server.
     * Return null if this HornetQMixIn instance is initialized for remote HornetQ Server.
     */
    public Configuration getConfiguration() {
        if (_embeddedJMS == null) {
            return null;
        }

        return _embeddedJMS.getHornetQServer().getConfiguration();
    }
    
    private static TransportConfiguration[] getTransports(final Configuration from) {
        final Collection<TransportConfiguration> transports = from.getConnectorConfigurations().values();
        return transports.toArray(new TransportConfiguration[]{});
    }

    @Override
    public void uninitialize() {
        try {
            HornetQMixIn.closeSession(_clientSession);
            HornetQMixIn.closeSessionFactory(_clientSessionFactory);
            HornetQMixIn.closeServerLocator(_serverLocator);
            
            HornetQMixIn.closeJMSSession(_jmsSession);
            HornetQMixIn.closeJMSConnection(_jmsConnection);
            HornetQMixIn.closeJMSConnectionFactory(_jmsConnectionFactory);
            
            if (_embeddedJMS != null) {
                _embeddedJMS.stop();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            // always clean JNDI context
            super.uninitialize();
        }
    }
    
    /**
     * By giving tests access to the {@link ClientSession} enables test to be able to
     * create queues, consumers, and producers.
     * 
     * @return {@link ClientSession} which can be used to create queues/topics, consumers/producers.
     */
    public ClientSession getClientSession() {
        if (_clientSession != null) {
            return _clientSession;
        }
        
        return createClientSession();
    }
    
    /**
     * Close the existing ClientSession if exists and create new one.
     * @return ClientSession instance
     */
    public ClientSession createClientSession() {
        closeSession(_clientSession);
        
        try {
            if (_serverLocator == null || _clientSessionFactory == null) {
                if (_startEmbedded) {
                    _serverLocator = HornetQClient.createServerLocatorWithoutHA(getTransports(getConfiguration()));
                    _clientSessionFactory = _serverLocator.createSessionFactory();
                } else {
                    Map<String, Object> params = new HashMap<String,Object>();
                    params.put(TransportConstants.HOST_PROP_NAME, _host);
                    params.put(TransportConstants.PORT_PROP_NAME, _port);
                    _serverLocator = HornetQClient.createServerLocatorWithoutHA(new TransportConfiguration(NettyConnectorFactory.class.getName(), params));
                    _clientSessionFactory = _serverLocator.createSessionFactory();
                }
            }
            
            _clientSession = _clientSessionFactory.createSession(_user,
                                                                _passwd,
                                                                false,
                                                                true,
                                                                true,
                                                                _serverLocator.isPreAcknowledge(),
                                                                _serverLocator.getAckBatchSize());
            _clientSession.start();
            return _clientSession;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a HornetQ {@link ClientMessage} with the passed-in String as the body.
     * 
     * @param body the String to be used as the messages payload(body)
     * @return ClientMessage with the passed-in String as its payload.
     */
    public ClientMessage createMessage(final String body) {
        final ClientMessage message = getClientSession().createMessage(true);
        message.getBodyBuffer().writeBytes(body.getBytes());
        return message;
    }
    
    /**
     * Load a resource from classpath with the passed-in String and create a HornetQ
     * {@link ClientMessage} using the content of resource as its payload.
     * 
     * @param path resource path
     * @return ClientMessage with the resource as its payload.
     */
    public ClientMessage createMessageFromResource(final String path) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        String payload = getStringFromInputStream(stream);
        return createMessage(payload);        
    }    
    
    /**
     * Reads the body of a HornetQ {@link ClientMessage} as an Object.
     * 
     * @param msg the HornetQ {@link ClientMessage}.
     * @return Object the object read.
     * @throws Exception if an error occurs while trying to read the body content.
     */
    public Object readObjectFromMessage(final ClientMessage msg) throws Exception {
        byte[] bytes = new byte[msg.getBodySize()];
        final HornetQBuffer bodyBuffer = msg.getBodyBuffer();
        bodyBuffer.readBytes(bytes);

        Object result = null;
        try {
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            result = in.readObject();
        } catch (Exception e) {
            _logger.warn("Caught an Exception during deserializing object. Then trying to read as String");
            _logger.debug("",e);
            result = new String(bytes);
        }
        
        return result;
    }

    /**
     * Reads the body of a HornetQ {@link ClientMessage} as an String and perform String compare between
     * the message payload and specified expected String.
     * @param msg the HornetQ {@link ClientMessage}
     * @param expected expected String in the message body
     * @return actual String in the message body
     * @throws Exception if an error occurs while trying to read the body content.
     */
    public String readMessageAndTestString(final ClientMessage msg, final String expected) throws Exception {
        Object payload = readObjectFromMessage(msg);
        Assert.assertTrue(payload instanceof String);
        Assert.assertEquals(expected, (String)payload);
        return (String)payload;
    }

    
    /**
     * By giving tests access to the {@link Session} enables test to be able to
     * create queues, consumers, and producers.
     * 
     * @return {@link Session} which can be used to create queues/topics, consumers/producers.
     */
    public Session getJMSSession() {
        if (_jmsSession != null) {
            return _jmsSession;
        }
        return createJMSSession();
    }

    /**
    * Close the existing Session if exists and create new one.
    * @return Session instance
    */
    public Session createJMSSession() {
        closeJMSSession(_jmsSession);
        
        try {
            if (_jmsConnectionFactory == null || _jmsConnection == null) {
                if (_startEmbedded) {
                    _jmsConnectionFactory = new HornetQConnectionFactory(false, getTransports(getConfiguration()));
                } else {
                    Map<String, Object> params = new HashMap<String,Object>();
                    params.put(TransportConstants.HOST_PROP_NAME, _host);
                    params.put(TransportConstants.PORT_PROP_NAME, _port);
                    _jmsConnectionFactory = new HornetQConnectionFactory(false, new TransportConfiguration(NettyConnectorFactory.class.getName(), params));
                }
                _jmsConnection = _jmsConnectionFactory.createConnection(_user, _passwd);
                _jmsConnection.start();
            }
            _jmsSession = _jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            return _jmsSession;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Creates a JMS {@link ObjectMessage} with the passed-in String as the body.
     * 
     * @param body the String to be used as the messages payload(body)
     * @return ObjectMessage with the passed-in String as its payload.
     * @throws JMSException if an error occurs while creating the message.
     */
    public ObjectMessage createJMSMessage(final String body) throws JMSException {
        return _jmsSession.createObjectMessage(body);
    }
    
    /**
     * Load a resource from classpath with the passed-in String and create a JMS
     * {@link Message} using the content of resource as its payload.
     * 
     * @param path resource path
     * @return ClientMessage with the resource as its payload.
     * @throws JMSException if an error occurs while creating the message.
     */
    public Message createJMSMessageFromResource(final String path) throws JMSException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        String payload = getStringFromInputStream(stream);
        return createJMSMessage(payload);        
    }    
    
    /**
     * Reads the body of a JMS {@link Message} as an Object.
     * 
     * @param msg the JMS {@link Message}.
     * @return the object read.
     * @throws JMSException if an error occurs while trying to read the body content.
     */
    public Object readObjectFromJMSMessage(final Message msg) throws JMSException {
        Assert.assertTrue(msg instanceof ObjectMessage);
        ObjectMessage objMsg = (ObjectMessage)msg;
        return objMsg.getObject();
    }
    
    /**
     * Reads the body of a JMS {@link Message} as a byte[].
     * 
     * @param msg JMS {@link Message}
     * @return the byte[] read.
     * @throws JMSException if an error occurs while trying to read the body content.
     */
    public byte[] readBytesFromJMSMessage(final Message msg) throws JMSException {
        Assert.assertTrue(msg instanceof BytesMessage);
        BytesMessage bsMsg = (BytesMessage)msg;
        if (bsMsg.getBodyLength() >= Integer.MAX_VALUE) {
            Assert.fail("Message body is too large[" + bsMsg.getBodyLength() + "]: extract it manually.");
        }
        byte[] ba = new byte[(int)bsMsg.getBodyLength()];
        bsMsg.readBytes(ba);
        return ba;
    }
    /**
     * Reads the body of a JMS {@link Message} as a String.
     * @param msg the JMS {@link Message}.
     * @return the string read.
     * @throws JMSException if an error occurs while trying to read the body content.
     */
    public String readStringFromJMSMessage(final Message msg) throws JMSException {
        if (msg instanceof TextMessage) {
            TextMessage txt = (TextMessage)msg;
            return txt.getText();
        } else if (msg instanceof ObjectMessage) {
            return (String)readObjectFromJMSMessage(msg);
        } else if (msg instanceof BytesMessage) {
            return new String(readBytesFromJMSMessage(msg));
        }
        throw new RuntimeException("The message body could not be extracted as String: " + msg);
    }
    /**
     * Reads the body of a JMS {@link Message} as an String and perform String compare between
     * the message payload and specified expected String.
     * @param msg the JMS {@link Message}.
     * @param expected expected String in the message body
     * @return actual String in the message body
     * @throws JMSException if an error occurs while trying to read the body content.
     */
    public String readJMSMessageAndTestString(final Message msg, final String expected) throws JMSException {
        String payload = readStringFromJMSMessage(msg);
        Assert.assertEquals(expected, (String)payload);
        return payload;
    }
    /**
     * Closes the passed-in {@link ServerLocator}.
     * 
     * @param serverLocator the {@link ServerLocator} instance to close.
     */
    public static void closeServerLocator(final ServerLocator serverLocator) {
        if (serverLocator != null) {
            serverLocator.close();
        }
    }
    
    /**
     * Closes the passed-in {@link ClientSessionFactory}.
     * 
     * @param factory the {@link ClientSessionFactory} instance to close.
     */
    public static void closeSessionFactory(final ClientSessionFactory factory) {
        if (factory != null) {
            factory.close();
        }
    }
    
    /**
     * Closes the passed-in {@link ClientSession}.
     * 
     * @param session the {@link ClientSession} instance to close.
     */
    public static void closeSession(final ClientSession session) {
        if (session != null) {
            try {
                session.close();
            } catch (final HornetQException ignore) {
                ignore.printStackTrace();
            }
        }
    }
    
    /**
     * Closes the passed-in {@link ClientConsumer}.
     * 
     * @param consumer the {@link ClientConsumer} instance to close.
     */
    public static void closeClientConsumer(final ClientConsumer consumer) {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (final HornetQException ignore) {
                ignore.printStackTrace();
            }
        }
    }
    
    /**
     * Closes the passed-in {@link ClientProducer}.
     * 
     * @param producer the {@link ClientProducer} to close.
     */
    public static void closeClientProducer(final ClientProducer producer) {
        if (producer != null) {
            try {
                producer.close();
            } catch (final HornetQException ignore) {
                ignore.printStackTrace();
            }
        }
    }

    /**
     * Closes the passed-in {@link ConnectionFactory}.
     * 
     * @param cf the {@link HornetQConnectionFactory} instance to close.
     */
    public static void closeJMSConnectionFactory(final HornetQConnectionFactory cf) {
        if (cf != null) {
            cf.close();
        }
    }
    
    /**
     * Closes the passed-in {@link Connection}.
     * 
     * @param conn the {@link Connection} instance to close.
     */
    public static void closeJMSConnection(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (JMSException ignore) {
                ignore.printStackTrace();
            }
        }
    }
    
    /**
     * Closes the passed-in {@link Session}.
     * 
     * @param session the {@link Session} instance to close.
     */
    public static void closeJMSSession(final Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException ignore) {
                ignore.printStackTrace();
            }
        }
    }

    /**
     * Closes the passed-in {@link MessageConsumer}.
     * 
     * @param consumer the {@link MessageConsumer} instance to close.
     */
    public static void closeJMSConsumer(final MessageConsumer consumer) {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (final JMSException ignore) {
                ignore.printStackTrace();
            }
        }
    }
    
    /**
     * Closes the passed-in {@link MessageProducer}.
     * 
     * @param producer the {@link MessageProducer} to close.
     */
    public static void closeJMSProducer(final MessageProducer producer) {
        if (producer != null) {
            try {
                producer.close();
            } catch (final JMSException ignore) {
                ignore.printStackTrace();
            }
        }
    }

    /**
     * Get {@link Queue} instance from core queue name.
     * @param name core queue name
     * @return {@link Queue} object
     */
    public static Queue getJMSQueue(final String name) {
        return new HornetQQueue(name);
    }
    
    /**
     * Create String object from InputStream
     * @param source source to read
     * @return String object extracted from InputStream
     */
    private String getStringFromInputStream(InputStream source) {
        Writer writer = new StringWriter();
        Reader reader = null;
        char[] buffer = new char[1024];
        try {
            
            reader = new BufferedReader(
                    new InputStreamReader(source, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (source != null) {
                    source.close();
                }
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
        }
        return writer.toString();
    }
}
