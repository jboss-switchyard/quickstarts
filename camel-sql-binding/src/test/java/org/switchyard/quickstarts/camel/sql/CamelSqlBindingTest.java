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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.quickstarts.camel.sql.binding.Greeting;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.NamingMixIn;

/**
 * SQL binding test - checks insert and retrieve operation.
 * 
 * @author Lukasz Dywicki
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = {CDIMixIn.class},
    scanners = BeanSwitchYardScanner.class
)
@RunWith(SwitchYardRunner.class)
public class CamelSqlBindingTest {

    private static Connection connection;

    private final static String RECEIVER = "Keith";
    private final static String SENDER = "David";

    @ServiceOperation("GreetingService")
    private Invoker invoker;

    private NamingMixIn mixin;

	private static JdbcDataSource dataSource;

    @BeforeClass
    public static void startUp() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:target/camel-sql-quickstart");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        connection = dataSource.getConnection();

        String createStatement = "CREATE TABLE greetings ("
            + "id INT PRIMARY KEY AUTO_INCREMENT, "
            + "name VARCHAR(255), "
            + "sender VARCHAR(255) "
        + ");";

        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS greetings");
        connection.createStatement().executeUpdate(createStatement);
    }

    @Before
    public void before() throws Exception {
        connection.createStatement().execute("TRUNCATE TABLE greetings");
        mixin.getInitialContext().bind("java:jboss/datasources/GreetDS", dataSource);
    }

    @After
    public void after() throws Exception {
        mixin.getInitialContext().unbind("java:jboss/datasources/GreetDS");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRetrieveGreetings() throws Exception {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO greetings (name, sender) VALUES (?,?)");
        statement.setString(1, RECEIVER);
        statement.setString(2, SENDER);
        assertEquals(1, statement.executeUpdate());

        Message message = invoker.operation("retrieve").sendInOut(null);
        List<Map<String, Object>> content = message.getContent(List.class);

        Map<String, Object> firstRow = content.iterator().next();
        Assert.assertEquals(RECEIVER, firstRow.get("name"));
        Assert.assertEquals(SENDER, firstRow.get("sender"));
    }

    @Test
    public void shouldStoreGreet() throws Exception {
        invoker.operation("store").sendInOnly(new Greeting(RECEIVER, SENDER));

        ResultSet result = connection.createStatement().executeQuery("SELECT * FROM greetings");
        assertTrue(result.next());
        assertEquals(RECEIVER, result.getString("name"));
        result.close();
    }


    @AfterClass
    public static void shutDown() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
    }

}
