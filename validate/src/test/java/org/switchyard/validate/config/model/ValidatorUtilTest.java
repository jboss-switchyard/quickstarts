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

package org.switchyard.validate.config.model;

import junit.framework.Assert;
import org.junit.Test;
import org.switchyard.annotations.Validator;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.ValidatorTypes;
import org.switchyard.validate.ValidatorUtil;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidatorUtilTest {

    @Test
    public void test_listValidations() {
        List<ValidatorTypes> validateTypes = ValidatorUtil.listValidations(TestValidator.class);

        Assert.assertEquals(4, validateTypes.size());
        Assert.assertEquals(JavaService.toMessageType(A.class), validateTypes.get(0).getName());
        Assert.assertEquals(JavaService.toMessageType(B.class), validateTypes.get(1).getName());
        Assert.assertEquals(QName.valueOf("X"), validateTypes.get(2).getName());
        Assert.assertEquals(QName.valueOf("Z"), validateTypes.get(3).getName());
    }

    @Test
    public void test_validate_interface_impl() {
        org.switchyard.validate.Validator validator = ValidatorUtil.newValidator(TestValidator.class, JavaService.toMessageType(A.class));

        Assert.assertTrue(validator instanceof TestValidator);
        Assert.assertTrue(validator.validate(new A()));
    }

    @Test
    public void test_validate_anno_no_types_defined() {
        org.switchyard.validate.Validator validator = ValidatorUtil.newValidator(TestValidator.class, JavaService.toMessageType(B.class));

        Assert.assertTrue(!(validator instanceof TestValidator));
        Assert.assertTrue(validator.validate(new B()));
        Assert.assertEquals(B.class, validator.getType());
    }

    @Test
    public void test_validate_unknown() {
        try {
            ValidatorUtil.newValidator(TestValidator.class, QName.valueOf("AAA"));
            Assert.fail("Expected Exception");
        } catch(RuntimeException e) {
            Assert.assertEquals("Error constructing Validator instance for class 'org.switchyard.validate.config.model.ValidatorUtilTest$TestValidator'.  " +
                    "Class does not support a validation for type 'AAA'.",
                    e.getMessage());
        }
    }

    @Test
    public void test_validate_anno_types_defined() {
        org.switchyard.validate.Validator validator = ValidatorUtil.newValidator(TestValidator.class, QName.valueOf("X"));

        Assert.assertTrue(!(validator instanceof TestValidator));
        Assert.assertTrue(validator.validate("X"));
    }


    @Test
    public void test_listNSdValidations() {
        List<ValidatorTypes> validateTypes = ValidatorUtil.listValidations(NSdTestValidator.class);

        Assert.assertEquals(1, validateTypes.size());
        Assert.assertEquals(new QName("http://b", "B"), validateTypes.get(0).getName());
    }

    public static class TestValidator extends BaseValidator {

        public TestValidator() {
            super(JavaService.toMessageType(A.class));
        }

        @Override
        public boolean validate(Object obj) {
            return obj instanceof Object;
        }

        @Validator
        public boolean validateB(B b) {
            return b instanceof B;
        }

        @Validator(name = "X")
        public boolean validateX(String x) {
            return x.equals("X");
        }

        @Validator(name = "Z")
        public boolean validateZ(B z) {
            return z instanceof B;
        }

    }

    public static class NSdTestValidator {

        @Validator(name = "{http://b}B")
        public boolean validateB(B b) {
            return b instanceof B;
        }
    }

    public static class A {

    }

    public static class B {

    }
}
