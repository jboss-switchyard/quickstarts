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
package org.switchyard.component.camel.config.model.ftp.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.file.remote.FtpEndpoint;
import org.apache.camel.component.file.remote.RemoteFileConfiguration.PathSeparator;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.ftp.CamelFtpBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelFtpBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelFtpBindingModelTest extends V1BaseCamelModelTest<V1CamelFtpBindingModel> {

    private static final String CAMEL_XML = "switchyard-ftp-binding-beans.xml";

    private static final String DIRECTORY = "test";
    private static final String HOST = "localhost";
    private static final Integer PORT = 203;
    private static final String USERNAME = "camel";
    private static final String PASSWORD = "secret";
    private static final Boolean BINARY = true;
    private static final Integer CONNECT_TIMEOUT = 10;
    private static final Boolean DISCONNECT = true;
    private static final Integer MAXIMUM_RECONNECT_ATTEMPTS = 10;
    private static final Integer RECONNECT_DELAY = 10;
    private static final PathSeparator SEPARATOR = PathSeparator.UNIX;
    private static final Boolean STEPWISE = true;
    private static final Boolean THROW_EXCEPTION_ON_CONNECT_FAIL = true;
    private static final Integer TIMEOUT = 10;
    private static final Integer SO_TIMEOUT = 10;
    private static final Boolean PASSIVE_MODE = true;
    private static final String SITE_COMMAND = "PWD";

    private static final String CAMEL_URI = "ftp://camel:secret@localhost:203/test?binary=true&" +
        "connectTimeout=10&disconnect=true&maximumReconnectAttempts=10&passiveMode=true&" +
        "reconnectDelay=10&separator=UNIX&siteCommand=PWD&soTimeout=10&stepwise=true&" +
        "throwExceptionOnConnectFailed=true&timeout=10";
    private static final String COMPONENT_URI = "ftp://camel:secret@localhost:203/test?binary=true&" +
        "connectTimeout=10&disconnect=true&maximumReconnectAttempts=10&reconnectDelay=10&" +
        "separator=UNIX&stepwise=true&throwExceptionOnConnectFailed=true&passiveMode=true&" +
        "timeout=10&soTimeout=10&siteCommand=PWD";

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelFtpBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        validateModel.assertValid();
        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testCamelEndpoint()  throws Exception {
        CamelFtpBindingModel model = getFirstCamelBinding(CAMEL_XML);

        FtpEndpoint<?> endpoint = getEndpoint(model, FtpEndpoint.class);
        assertEquals(endpoint.getConfiguration().getProtocol(), V1CamelFtpBindingModel.FTP);
        assertEquals(CAMEL_URI, endpoint.getEndpointUri().toString());
    }

    private V1CamelFtpBindingModel createModel() {
        return (V1CamelFtpBindingModel) new V1CamelFtpBindingModel()
            .setPassiveMode(PASSIVE_MODE)
            .setTimeout(TIMEOUT)
            .setSoTimeout(SO_TIMEOUT)
            .setSiteCommand(SITE_COMMAND)
            .setDirectory(DIRECTORY)
            .setHost(HOST)
            .setPort(PORT)
            .setUsername(USERNAME)
            .setPassword(PASSWORD)
            .setBinary(BINARY)
            .setConnectionTimeout(CONNECT_TIMEOUT)
            .setThrowExceptionOnConnectFailed(THROW_EXCEPTION_ON_CONNECT_FAIL)
            .setStepwise(STEPWISE)
            .setSeparator(SEPARATOR.name())
            .setReconnectDelay(RECONNECT_DELAY)
            .setMaximumReconnectAttempts(MAXIMUM_RECONNECT_ATTEMPTS)
            .setDisconnect(DISCONNECT);
    }

    private void assertModel(CamelFtpBindingModel model) {
        assertEquals(PASSIVE_MODE, model.isPassiveMode());
        assertEquals(TIMEOUT, model.getTimeout());
        assertEquals(SO_TIMEOUT, model.getSoTimeout());
        assertEquals(SITE_COMMAND, model.getSiteCommand());
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(USERNAME, model.getUsername());
        assertEquals(PASSWORD, model.getPassword());
        assertEquals(BINARY, model.isBinary());
        assertEquals(CONNECT_TIMEOUT, model.getConnectTimeout());
        assertEquals(THROW_EXCEPTION_ON_CONNECT_FAIL, model.isThrowExceptionOnConnectFailed());
        assertEquals(STEPWISE, model.isStepwise());
        assertEquals(SEPARATOR, model.getSeparator());
        assertEquals(RECONNECT_DELAY, model.getReconnectDelay());
        assertEquals(MAXIMUM_RECONNECT_ATTEMPTS, model.getMaximumReconnectAttempts());
        assertEquals(DISCONNECT, model.getDisconnect());
    }
}
