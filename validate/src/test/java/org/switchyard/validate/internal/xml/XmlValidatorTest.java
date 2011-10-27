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

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.validate.Validator;
import org.switchyard.validate.AbstractValidatorTestCase;
import org.switchyard.validate.xml.XmlValidator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class XmlValidatorTest extends AbstractValidatorTestCase {

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
        Validator validator = getValidator("sw-config-no-schemafile.xml");
        try {
            validator.validate(new DefaultMessage().setContent(new StringReader("<order type='A' />")));
        } catch(RuntimeException e) {
            Assert.assertEquals("Error during validation: schemaFile must be specified for 'XML_SCHEMA'.", e.getMessage());
        }
    }

    @Test
    public void test_invalid_schemafile() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-invalid-schemafile.xml");
        try {
            validator.validate(new DefaultMessage().setContent(new StringReader("<order type='A' />")));
        } catch(RuntimeException e) {
            Assert.assertEquals("Error during validation with '/org/switchyard/validate/internal/xml/person-invalid.xsd' as 'XML_SCHEMA'.", e.getMessage());
        }
    }

    @Test
    public void test_valid_xml() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-xmlv-01.xml");
        Assert.assertTrue(validator.validate(new DefaultMessage().setContent(new StringReader("<person name='foo' age='50' />"))));
    }

    @Test
    public void test_invalid_xml() throws IOException, SAXException {
        Validator validator = getValidator("sw-config-xmlv-01.xml");
        try {
            validator.validate(new DefaultMessage().setContent(new StringReader("<person name='foo'/>")));
        } catch (RuntimeException e) {
            Assert.assertEquals("Error during validation with '/org/switchyard/validate/internal/xml/person.xsd' as 'XML_SCHEMA'.", e.getMessage());
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
