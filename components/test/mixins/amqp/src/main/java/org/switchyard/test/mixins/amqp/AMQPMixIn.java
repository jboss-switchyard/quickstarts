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
package org.switchyard.test.mixins.amqp;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.client.AMQDestination;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.SwitchYardException;
import org.switchyard.test.MixInDependencies;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * AMQMixIn helps developers test our AMQP Bindings on SwitchYard applications.
 *
 * The following files must be present in the classpath:
 *
 * - amqp.properties file is used to define the following parameters:
 *      - qpidConnectionfactory: connection factory url (e.g. qpidConnectionfactory = amqp://guest:guest@/test?brokerlist='tcp://localhost:5672')
 *      - queueExchange: queue exchange to be used as a destination (e.g. BURL:direct://amq.direct//ping?routingkey='#')
 *      - topicExchange: topic exchange to be used as a destination (e.g. BURL:topic://amq.topic//MyTopic?routingkey='#')
 *
 * - config.xml file used for Apache QPID configuration.
 * - virtualhosts.xml file used for Apache QPID virtual host configurations.
 * - log4j.xml file used for Apache Log4J configuration.
 * - passwd file used for Apache QPID user credentials.
 *
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 * Date: 11/11/12
 * Time: 7:22 PM
 */
@MixInDependencies(required = {NamingMixIn.class})
public class AMQPMixIn extends AbstractTestMixIn {

    /**
     * Connection factory location.
     */
    public static final String JNDI_CONNECTION_FACTORY = "java:jboss/qpidConnectionFactory";

    /**
     * Test queue.
     */
    public static final String DEFAULT_QUEUE_JNDI_LOCATION = "java:jboss/queueExchange";

    /**
     * Topic location.
     */
    public static final String DEFAULT_TOPIC_JNDI_LOCATION = "java:jboss/topicExchange";

    //amp.properties located in the classpath of the running Thread
    private static final String AMQP_PROPERTIES_FILE = "/amqp.properties";
    private static final String LOG_XML = "/log4j.xml";
    private static final String CONFIG_XML = "/config.xml";
    private static final String QPID_WORK_DIRECTORY = "/qpid";
    private static final String SYS_PROP_QPID_HOME = "QPID_HOME";
    private static final String SYS_PROP_QPID_WORK = "QPID_WORK";
    protected static final String AMQP_PROPS_QPID_CONNECTIONFACTORY = "qpidConnectionfactory";
    protected static final String AMQP_PROPS_QUEUE_EXCHANGE = "queueExchange";
    protected static final String AMPQ_PROPS_TOPIC_EXCHANGE = "topicExchange";

    private Broker _broker;

    private Properties _properties;
    private String _qpidConnectionFactory;
    private String _qpidQueueExchange;
    private String _qpidTopicExchange;

    /**
     * Initialize new instance of mixin and read configuration.
     * 
     * @throws Exception In case of initialization failure.
     */
    public AMQPMixIn() throws Exception {
        _properties = new Properties();
        InputStream is = this.getClass().getResourceAsStream(AMQP_PROPERTIES_FILE);
        try {
            _properties.load(is);
            _qpidConnectionFactory = _properties.getProperty(AMQP_PROPS_QPID_CONNECTIONFACTORY);
        } finally {
            is.close();
        }
        if (_qpidConnectionFactory == null) {
            throw new SwitchYardException("No connection factory configured. Please set "+ AMQP_PROPS_QPID_CONNECTIONFACTORY
                + " in the "+AMQP_PROPERTIES_FILE+" file found in the class path of your application");
        }

        _qpidQueueExchange = _properties.getProperty(AMQP_PROPS_QUEUE_EXCHANGE);
        _qpidTopicExchange = _properties.getProperty(AMPQ_PROPS_TOPIC_EXCHANGE);

        if (_qpidQueueExchange == null && _qpidTopicExchange == null) {
            throw new SwitchYardException("No topic or queue configured. Please set either one by using "
                + AMQP_PROPS_QUEUE_EXCHANGE + " or " + AMPQ_PROPS_TOPIC_EXCHANGE + " in your " + AMQP_PROPERTIES_FILE + " properties file.");
        }
    }

    /**
     * Gets connection factory instance.
     * 
     * @return Connection factory.
     * @throws NamingException In case of lookup failure.
     */
    public ConnectionFactory getConnectionFactory() throws NamingException {
        return (ConnectionFactory) new InitialContext().lookup(JNDI_CONNECTION_FACTORY);
    }

    /**
     * Lookup given destination in JNDI tree.
     * 
     * @param destinationName Destination name.
     * @return Destination instance.
     * @throws NamingException If lookup fails.
     */
    public Destination getDestination(String destinationName) throws NamingException {
        return (Destination) new InitialContext().lookup(destinationName);
    }

    @Override
    public void before(AbstractDeployment deployment) {
        super.before(deployment);
    }

    @Override
    public void after(AbstractDeployment deployment) {
        super.after(deployment);
    }

    @Override
    public void initialize() {
        File file = new File(this.getClass().getResource("/").getFile());

        System.setProperty(SYS_PROP_QPID_HOME, file.getPath());
        System.setProperty(SYS_PROP_QPID_WORK, file.getParent() + QPID_WORK_DIRECTORY);

        BrokerOptions options = new BrokerOptions();

        String configFile = getClass().getResource(CONFIG_XML).getFile();
        options.setConfigFile(configFile);
        URL log4j = getClass().getResource(LOG_XML);
        if (log4j != null) {
            options.setLogConfigFile(log4j.getFile());
        }

        _broker = new Broker();
        try {
            _broker.startup(options);
            Context context = new InitialContext();
            context.bind(JNDI_CONNECTION_FACTORY, new AMQConnectionFactory(_qpidConnectionFactory));
            if (_qpidQueueExchange != null) {
                context.bind(DEFAULT_QUEUE_JNDI_LOCATION, AMQDestination.createDestination(_qpidQueueExchange));
            }
            if (_qpidTopicExchange != null) {
                context.bind(DEFAULT_TOPIC_JNDI_LOCATION, AMQDestination.createDestination(_qpidTopicExchange));
            }
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    @Override
    public void uninitialize() {
        if (_broker != null) {
            _broker.shutdown();
        }
    }

}
