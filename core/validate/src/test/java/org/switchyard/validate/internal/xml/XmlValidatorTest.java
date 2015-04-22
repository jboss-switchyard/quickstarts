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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jboss.logging.Logger;
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
            boolean exceptionText = e.getMessage().contains("SWITCHYARD017219");
            Assert.assertTrue(exceptionText);
        }
    }

    @Test
    public void test_no_schemafile() throws IOException, SAXException {
        try {
            getValidator("sw-config-no-schemafile.xml");
        } catch(RuntimeException e) {
            boolean exceptionText = e.getMessage().contains("SWITCHYARD017221");
            Assert.assertTrue(exceptionText);
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
        source = "<person2 firstName='ﾀﾛｳ' lastName='ﾔﾏﾀﾞ' age='50' />";
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

    @Test
    public void test_dtd_valid_xml_two() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-dtd-two.xml");
        String source = "<!DOCTYPE nn:memo SYSTEM \"src/test/resources/org/switchyard/validate/internal/xml/memo.dtd\">"
                + "<nn:memo xmlns:nn=\"urn:switchyard-quickstart:dtd-example:0.1.0\">"
                + "<nn:to>Garfield</nn:to>"
                + "<nn:from>Odie</nn:from>"
                + "<nn:body>I love lasagna!</nn:body>"
                + "</nn:memo>";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    /**
     * Tests included DTD.
     * @throws Exception exception
     */
    @Test
    public void test_dtd_valid_xml_three() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-dtd-two.xml");
        String source = "<!DOCTYPE nn:memo ["
                + "<!ELEMENT nn:memo (nn:to,nn:from,nn:body)>"
                + "<!ATTLIST nn:memo xmlns:nn CDATA #FIXED \"urn:switchyard-quickstart:dtd-example:0.1.0\">"
                + "<!ELEMENT nn:to (#PCDATA)>"
                + "<!ELEMENT nn:from (#PCDATA)>"
                + "<!ELEMENT nn:heading (#PCDATA)>"
                + "<!ELEMENT nn:body (#PCDATA)>"
                + "]>"
                + "<nn:memo xmlns:nn=\"urn:switchyard-quickstart:dtd-example:0.1.0\">"
                + "<nn:to>Garfield</nn:to>"
                + "<nn:from>Odie</nn:from>"
                + "<nn:body>I love lasagna!</nn:body>"
                + "</nn:memo>";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (!result.isValid()) {
            Assert.fail(result.getDetail());
        }
        Assert.assertNull(result.getDetail());
    }

    /**
     * Negative test, tests invalid DTD.
     * @throws Exception exception
     */
    @Test
    public void test_dtd_valid_xml_four() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-dtd-two.xml");
        String source = "<!DOCTYPE nn:memo ["
                + "<!ELEMENT nn:memo (to,from,body)>"
                + "<!ATTLIST nn:memo xmlns:nn CDATA #FIXED \"urn:switchyard-quickstart:dtd-example:0.1.0\">"
                + "<!ELEMENT nn:to (#PCDATA)>"
                + "<!ELEMENT nn:from (#PCDATA)>"
                + "<!ELEMENT nn:body (#PCDATA)>"
                + "]>"
                + "<nn:memo xmlns:nn=\"urn:switchyard-quickstart:dtd-example:0.1.0\">"
                + "<nn:to>Garfield</nn:to>"
                + "<nn:from>Odie</nn:from>"
                + "<nn:body>I love lasagna!</nn:body>"
                + "</nn:memo>";
        ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        if (result.isValid()) {
            Assert.fail(result.getDetail());
        }
    }


    /**
     * Negative test, tests invalid schema name.
     * @throws Exception exception
     */
    @Test
    public void test_dtd_valid_xml_five() throws Exception {
        Validator validator = getValidator("sw-config-xmlv-dtd-two.xml");
        String source = "<!DOCTYPE nn:memo SYSTEM \"failmemo.dtd\">"
                + "<nn:memo xmlns:nn=\"urn:switchyard-quickstart:dtd-example:0.1.0\">"
                + "<nn:to>Garfield</nn:to>"
                + "<nn:from>Odie</nn:from>"
                + "<nn:body>I love lasagna!</nn:body>"
                + "</nn:memo>";

        boolean flag = false;
        try {
            ValidationResult result = validator.validate(new DefaultMessage().setContent(source));
        } catch (Exception fnfe) {
            flag = true;
        }
        if (!flag) {
            Assert.fail("Somehow found DTD that we should not have.");
        }
    }

    protected Validator getValidator(String config) throws IOException {
        Validator validator = super.getValidator(config);

        if(!(validator instanceof XmlValidator)) {
            Assert.fail("Not an instance of XmlValidator.");
        }

        return validator;
    }
}
