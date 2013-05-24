package org.switchyard.component.resteasy.resource;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for StandaloneResourcePublisher
 *
 * @author Luis del Toro <luisdeltoro@gmail.com>
 */
public class StandaloneResourcePublisherTest {

    private static final int TEST_PORT = 9191;

    @Before
    public void setUp() {
        // make sure that the system property is not set
        System.clearProperty(StandaloneResourcePublisher.DEFAULT_PORT_PROPERTY);
    }
    @Test
    public void useDefaultPort() {
        final int port = StandaloneResourcePublisher.getPort();
        assertThat(port, is(equalTo(StandaloneResourcePublisher.DEFAULT_PORT)));
    }

    @Test
    public void useConfiguredPort() {
        System.setProperty(StandaloneResourcePublisher.DEFAULT_PORT_PROPERTY, Integer.toString(TEST_PORT));
        final int port = StandaloneResourcePublisher.getPort();
        assertThat(port, is(equalTo(TEST_PORT)));
    }
}
