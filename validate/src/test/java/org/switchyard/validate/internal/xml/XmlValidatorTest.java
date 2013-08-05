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

package org.switchyard.validate.internal.xml;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.SwitchYardException;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.Validator;
import org.switchyard.validate.AbstractValidatorTestCase;
import org.switchyard.validate.xml.internal.XmlValidator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class XmlValidatorTest extends AbstractValidatorTestCase {
    private static final Logger LOGGER = Logger.getLogger(XmlValidatorTest.class);
   
    @Test
    public void test_no_schematype() throws IOException {
        try {
            getValidator("sw-config-no-schematype.xml");
        } catch(RuntimeException e) {
            Assert.assertEquals("Could not instantiate XmlValidator: schemaType must be specified.", e.getMessage());
        }
    }

    @Test
    public void test_no_schemafile() throws IOException, SAXException {
        try {
            getValidator("sw-config-no-schemafile.xml");
        } catch(RuntimeException e) {
            Assert.assertEquals("schema file must be specified for XML_SCHEMA validation.", e.getMessage());
        }
    }

    @Test(expected=SwitchYardException.class)
    public void test_invalid_schemafile() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-invalid-schemafile.xml");
        ValidationResult result = validator.validate(new DefaultMessage().setContent("<order type='A' />"));
    }

    @Test
    public void test_valid_xml() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-xmlv-01.xml");
        String source = "<person name='foo' age='50' />";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        source = "<person2 firstName='foo' lastName='bar' age='50' />";
        result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    @Test
    public void test_invalid_xml() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-xmlv-01.xml");
        String source = "<person name='foo'/>";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        Assert.assertFalse(result.isValid());
        LOGGER.info(result.getDetail());
        Assert.assertTrue(result.getDetail().startsWith("1 validation error(s)"));
    }

    @Test
    public void test_namespaceaware_valid_xml() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-namespace.xml");
        String source = "<import:person.2 xmlns:import=\"switchyard-validate-test:import:1.0\" firstName='foo' lastName='bar' name='foo bar' age='50' />";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    @Test
    public void test_namespaceaware_catalog_valid_xml() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-namespace-catalog.xml");
        String source = "<import:person.2 xmlns:import=\"switchyard-validate-test:import:1.0\" firstName='foo' lastName='bar' name='foo bar' age='50' />";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    @Test
    public void test_dtd_valid_xml() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-dtd.xml");
        String source = "<!DOCTYPE person SYSTEM \"src/test/resources/org/switchyard/validate/internal/xml/person.dtd\"> <person name='foo' age='50' />";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }
    
    protected Validator getValidator(String config) throws IOException {
        Validator validator = super.getValidator(config);

        if(!(validator instanceof XmlValidator)) {
            Assert.fail("Not an instance of XmlValidator.");
        }

        return validator;
    }
}
