/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.transform.internal.smooks;

import java.io.IOException;
import java.io.InputStream;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.transform.AbstractTransformerTestCase;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.smooks.internal.SmooksTransformFactory;
import org.switchyard.transform.smooks.internal.SmooksTransformer;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksTransformerTest extends AbstractTransformerTestCase {

    @Test
    public void test_no_export() throws IOException {
        try {
            getTransformer("sw-config-01.xml");
        } catch(RuntimeException e) {
        	boolean exceptionMatches = e.getMessage().contains("SWITCHYARD016814");
        	Assert.assertTrue(exceptionMatches);
        }
    }

    @Test
    public void test_invalid_export() throws IOException, SAXException {
        try {
            getTransformer("sw-config-02.xml");
        } catch(RuntimeException e) {
        	boolean exceptionMatches = e.getMessage().contains("SWITCHYARD016815");
        	Assert.assertTrue(exceptionMatches);
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

    protected Transformer getTransformer(String config) throws IOException {
        Transformer transformer = super.getTransformer(config);

        if(!(transformer instanceof SmooksTransformer)) {
            Assert.fail("Not an instance of SmooksTransformer.");
        }

        return transformer;
    }
}
