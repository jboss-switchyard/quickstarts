package org.switchyard.component.http.endpoint;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for StandaloneEndpointPublisher
 *
 * @author Luis del Toro <luisdeltoro@gmail.com>
 */
public class StandaloneEndpointPublisherTest {

    private static final int TEST_PORT = 9191;

    @Before
    public void setUp() {
        // make sure that the system property is not set
        System.clearProperty(StandaloneEndpointPublisher.DEFAULT_PORT_PROPERTY);
    }
    @Test
    public void useDefaultPort() {
        final int port = StandaloneEndpointPublisher.getPort();
        assertThat(port, is(equalTo(StandaloneEndpointPublisher.DEFAULT_PORT)));
    }

    @Test
    public void useConfiguredPort() {
        System.setProperty(StandaloneEndpointPublisher.DEFAULT_PORT_PROPERTY, Integer.toString(TEST_PORT));
        final int port = StandaloneEndpointPublisher.getPort();
        assertThat(port, is(equalTo(TEST_PORT)));
    }
}
