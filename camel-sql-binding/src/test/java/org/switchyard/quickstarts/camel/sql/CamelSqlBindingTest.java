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
        bindDataSource(namingMixIn.getInitialContext(), "java:jboss/datasources/ExampleDS", dataSource);
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
