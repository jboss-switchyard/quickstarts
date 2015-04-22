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
package org.switchyard.quickstarts.camel.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.test.SwitchYardRunner;

/**
 * Base class for SQL binding tests.
 * 
 * @author Lukasz Dywicki
 */
@RunWith(SwitchYardRunner.class)
public abstract class CamelSqlBindingTest {

    private static JdbcDataSource dataSource;
    protected static Connection connection;

    protected final static String RECEIVER = "Keith";
    protected final static String SENDER = "David";
    private static NamingMixIn namingMixIn;

    @BeforeClass
    public static void startUp() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        connection = dataSource.getConnection();

        String createStatement = "CREATE TABLE greetings ("
            + "id INT PRIMARY KEY AUTO_INCREMENT, "
            + "receiver VARCHAR(255), "
            + "sender VARCHAR(255) "
            + ");";

        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS greetings");
        connection.createStatement().executeUpdate(createStatement);

        namingMixIn = new NamingMixIn();
        namingMixIn.initialize();
        bindDataSource(namingMixIn.getInitialContext(), "java:jboss/myDS", dataSource);
    }

    private static void bindDataSource(InitialContext context, String name, DataSource ds) throws Exception {
        try {
            context.bind(name, ds);
        } catch (NameAlreadyBoundException e) {
            e.getMessage(); // ignore
        }
    }

    @AfterClass
    public static void shutDown() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
        namingMixIn.uninitialize();
    }

}
