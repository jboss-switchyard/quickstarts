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

package org.switchyard.validate.internal.xml;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.Validator;
import org.switchyard.validate.AbstractValidatorTestCase;
import org.switchyard.validate.xml.XmlValidator;
import org.xml.sax.InputSource;
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
        InputSource source = new InputSource(new StringReader("<order type='A' />"));
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
    }

    @Test
    public void test_valid_xml() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-xmlv-01.xml");
        InputSource source = new InputSource(new StringReader("<person name='foo' age='50' />"));
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        source = new InputSource(new StringReader("<person2 firstName='foo' lastName='bar' age='50' />"));
        result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    @Test
    public void test_invalid_xml() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-xmlv-01.xml");
        InputSource source = new InputSource(new StringReader("<person name='foo'/>"));
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        Assert.assertFalse(result.isValid());
        LOGGER.info(result.getDetail());
        Assert.assertTrue(result.getDetail().startsWith("1 validation error(s)"));
    }

    @Test
    public void test_namespaceaware_valid_xml() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-namespace.xml");
        InputSource source = new InputSource(new StringReader("<import:person.2 xmlns:import=\"switchyard-validate-test:import:1.0\" firstName='foo' lastName='bar' name='foo bar' age='50' />"));
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    @Test
    public void test_namespaceaware_catalog_valid_xml() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-namespace-catalog.xml");
        InputSource source = new InputSource(new StringReader("<import:person.2 xmlns:import=\"switchyard-validate-test:import:1.0\" firstName='foo' lastName='bar' name='foo bar' age='50' />"));
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }
    
    @Test
    public void test_dtd_valid_xml() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-dtd.xml");
        InputSource source = new InputSource(new StringReader("<!DOCTYPE person SYSTEM \"src/test/resources/org/switchyard/validate/internal/xml/person.dtd\"> <person name='foo' age='50' />"));
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
