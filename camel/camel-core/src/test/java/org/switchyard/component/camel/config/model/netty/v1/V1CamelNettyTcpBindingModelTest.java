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
package org.switchyard.component.camel.config.model.netty.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.netty.CamelNettyTcpBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelNettyBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelNettyTcpBindingModelTest extends V1BaseCamelModelTest<V1CamelNettyTcpBindingModel> {

    private static final String CAMEL_XML = "switchyard-netty-tcp-binding-beans.xml";

    private static final String HOST = "google.com";
    private static final Integer PORT = 10230;
    private static final Long RECEIVE_BUFFER_SIZE = 1024l;
    private static final Long SEND_BUFFER_SIZE = 128l;
    private static final Boolean REUSE_ADDRESS = true;
    private static final Boolean ALLOW_DEFAULT_CODEC = false;
    private static final Integer WORKER_COUNT = 10;
    private static final Boolean SYNC = false;
    private static final Boolean DISCONNECT = true;
    private static final Boolean TEXTLINE = false;
    private static final Boolean TCP_NO_DELAY = true;
    private static final Boolean KEEP_ALIVE = false;

    private static final String COMPONENT_URI = "netty:tcp://google.com:10230?" +
        "receiveBufferSize=1024&sendBufferSize=128&reuseAddress=true&allowDefaultCodec=false&" +
        "workerCount=10&sync=false&disconnect=true&textline=false&tcpNoDelay=true&" +
        "keepAlive=false";

    @Test
    public void validateCamelBinding() throws Exception {
        final V1CamelNettyTcpBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void verifyProtocol() {
        V1CamelNettyTcpBindingModel model = new V1CamelNettyTcpBindingModel();
        model.setHost(HOST).setPort(PORT);

        model.assertModelValid();
        String uri = model.getComponentURI().toString();
        assertTrue(uri.startsWith("netty:tcp://"));
        assertTrue(model.validateModel().isValid());
    }


    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    private V1CamelNettyTcpBindingModel createModel() {
        return (V1CamelNettyTcpBindingModel) new V1CamelNettyTcpBindingModel()
            .setTextline(TEXTLINE)
            .setTcpNoDelay(TCP_NO_DELAY)
            .setKeepAlive(KEEP_ALIVE)
            .setHost(HOST)
            .setPort(PORT)
            .setReceiveBufferSize(RECEIVE_BUFFER_SIZE)
            .setSendBufferSize(SEND_BUFFER_SIZE)
            .setReuseAddress(REUSE_ADDRESS)
            .setAllowDefaultCodec(ALLOW_DEFAULT_CODEC)
            .setWorkerCount(WORKER_COUNT)
            .setSync(SYNC)
            .setDisconnect(DISCONNECT);
    }

    private void assertModel(CamelNettyTcpBindingModel model) {
        assertEquals(TEXTLINE, model.isTextline());
        assertEquals(TCP_NO_DELAY, model.isTcpNoDelay());
        assertEquals(KEEP_ALIVE, model.isKeepAlive());
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(RECEIVE_BUFFER_SIZE, model.getReceiveBufferSize());
        assertEquals(SEND_BUFFER_SIZE, model.getSendBufferSize());
        assertEquals(REUSE_ADDRESS, model.isReuseAddress());
        assertEquals(ALLOW_DEFAULT_CODEC, model.isAllowDefaultCodec());
        assertEquals(WORKER_COUNT, model.getWorkerCount());
        assertEquals(SYNC, model.isSync());
        assertEquals(DISCONNECT, model.isDisconnect());
    }

}