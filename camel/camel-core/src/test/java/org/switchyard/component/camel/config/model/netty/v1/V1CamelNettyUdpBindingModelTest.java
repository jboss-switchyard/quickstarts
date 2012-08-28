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
import org.switchyard.component.camel.config.model.netty.CamelNettyUdpBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelNettyBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelNettyUdpBindingModelTest extends V1BaseCamelModelTest<V1CamelNettyUdpBindingModel> {

    private static final String CAMEL_XML = "switchyard-netty-udp-binding-beans.xml";

    private static final String HOST = "google.com";
    private static final Integer PORT = 10231;
    private static final Boolean BROADCAST = true;

    private static final String COMPONENT_URI = "netty:udp://google.com:10231?broadcast=true";

    @Test
    public void validateCamelBinding() throws Exception {
        final V1CamelNettyUdpBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void verifyProtocol() {
        V1CamelNettyUdpBindingModel model = new V1CamelNettyUdpBindingModel();
        model.setHost(HOST).setPort(PORT);

        model.assertModelValid();
        String uri = model.getComponentURI().toString();
        assertTrue(uri.startsWith("netty:udp://"));
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

    private V1CamelNettyUdpBindingModel createModel() {
        return (V1CamelNettyUdpBindingModel) new V1CamelNettyUdpBindingModel()
            .setBroadcast(BROADCAST)
            .setHost(HOST)
            .setPort(PORT);
    }

    private void assertModel(CamelNettyUdpBindingModel model) {
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(BROADCAST, model.isBroadcast());
    }

}