/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.transform.internal.smooks;

import java.io.IOException;
import java.io.InputStream;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.smooks.internal.SmooksTransformFactory;
import org.switchyard.transform.smooks.internal.SmooksTransformer;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksTransformerTest {

    @Test
    public void test_no_export() throws IOException {
        try {
            getTransformer("sw-config-01.xml");
        } catch(RuntimeException e) {
            Assert.assertEquals("Invalid Smooks configuration file.  Must define an <core:exports> section with a single <core:export>.  See Smooks User Guide.", e.getMessage());
        }
    }

    @Test
    public void test_invalid_export() throws IOException, SAXException {
        try {
            getTransformer("sw-config-02.xml");
        } catch(RuntimeException e) {
            Assert.assertEquals("Unsupported Smooks <core:export> type 'java.io.StringReader'.  Only supports StringResult or JavaResult.", e.getMessage());
        }
    }

    @Test
    public void test_StringResult() throws IOException, SAXException {
        Transformer transformer = getTransformer("sw-config-03.xml");

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual("<order type='B' />", transformer.transform("<order type='A' />").toString());
    }

    @Test
    public void test_JavaResult() throws IOException, SAXException {
        Transformer transformer = getTransformer("sw-config-04.xml");

        Person person = (Person) transformer.transform("<person name='Max' age='50' />");
        Assert.assertEquals("Max", person.getName());
        Assert.assertEquals(50, person.getAge());
    }

    private Transformer getTransformer(String config) throws IOException {
        InputStream swConfigStream = Classes.getResourceAsStream(config, getClass());

        if(swConfigStream == null) {
            Assert.fail("null config stream.");
        }

        SwitchYardModel switchyardConfig;
        try {
            switchyardConfig = new ModelResource<SwitchYardModel>().pull(swConfigStream);
        } finally {
            swConfigStream.close();
        }

        TransformsModel transforms = switchyardConfig.getTransforms();

        SmooksTransformModel smooksTransformModel = (SmooksTransformModel) transforms.getTransforms().get(0);

        if(smooksTransformModel == null) {
            Assert.fail("No smooks config.");
        }

        Transformer transformer = SmooksTransformFactory.newTransformer(smooksTransformModel);

        if(!(transformer instanceof SmooksTransformer)) {
            Assert.fail("Not an instance of SmooksTransformer.");
        }

        return transformer;
    }
}
