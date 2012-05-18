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
        bind(initialContext, "java:jboss/datasources/ExampleDS", ds);

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
