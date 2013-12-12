/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.amqp.binding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.client.AMQConnectionFactory;

/**
 * Simple qpid client.
 */
public class QpidClient {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Properties properties = new Properties();
        properties.load(QpidClient.class.getResourceAsStream("/amqp.properties"));

        AMQConnectionFactory connectionFactory = new AMQConnectionFactory(properties.getProperty("qpidConnectionfactory"));
        Connection connection = connectionFactory.createConnection("guest", "guest");
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        System.out.println("Type your name:");
        String name = reader.readLine();
        TextMessage textMessage = session.createTextMessage(name);
        MessageProducer producer = session.createProducer(session.createQueue("ping"));
        producer.send(textMessage);
        System.out.println("Send message " + name);

        session.close();
        connection.stop();
    }

}
