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
package org.switchyard.quickstarts.camel.jpa.binding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.switchyard.component.test.mixins.naming.NamingMixIn;

public abstract class CamelJpaBindingTest {

    protected static final String RECEIVER = "Receiver";
    protected static final String SENDER = "Sender";
    protected static Connection connection;
    private static NamingMixIn namingMixIn;

    @BeforeClass
    public static void beforeClass() throws Exception {
        namingMixIn = new NamingMixIn();
        namingMixIn.initialize();

        connection = DriverManager.getConnection("jdbc:h2:mem:events-jpa-quickstart", "sa", "sa");

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:events-jpa-quickstart");
        ds.setUser("sa");
        ds.setPassword("sa");

        InitialContext initialContext = new InitialContext();
        bind(initialContext, System.getProperty("jpa.persistence.datasource.name"), ds);

    }

    private static void bind(InitialContext initialContext, String name, Object object) throws NamingException {
        Name jndiName = initialContext.getNameParser("").parse(name);
        initialContext.bind(jndiName, object);
    }

    @AfterClass
    public static void shutDown() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
        namingMixIn.uninitialize();
    }
}
