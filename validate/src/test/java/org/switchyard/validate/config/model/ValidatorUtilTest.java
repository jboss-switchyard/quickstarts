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

import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.annotations.Validator;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.internal.ValidatorTypes;
import org.switchyard.validate.internal.ValidatorUtil;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidatorUtilTest {

    @Test
    public void test_listValidations() {
        List<ValidatorTypes> validateTypes = ValidatorUtil.listValidations(TestValidator.class);

        Assert.assertEquals(4, validateTypes.size());
        Assert.assertEquals(QName.valueOf("X"), validateTypes.get(0).getName());
        Assert.assertEquals(QName.valueOf("Z"), validateTypes.get(1).getName());
        Assert.assertEquals(JavaTypes.toMessageType(A.class), validateTypes.get(2).getName());
        Assert.assertEquals(JavaTypes.toMessageType(B.class), validateTypes.get(3).getName());
    }

    @Test
    public void test_validate_interface_impl() {
        org.switchyard.validate.Validator validator = ValidatorUtil.newValidator(TestValidator.class, JavaTypes.toMessageType(A.class));

        Assert.assertTrue(validator instanceof TestValidator);
        ValidationResult result = validator.validate(new A());
        Assert.assertTrue(result.isValid());
        Assert.assertNull(result.getDetail());
    }

    @Test
    public void test_validate_anno_no_types_defined() {
        org.switchyard.validate.Validator validator = ValidatorUtil.newValidator(TestValidator.class, JavaTypes.toMessageType(B.class));

        Assert.assertTrue(!(validator instanceof TestValidator));
        ValidationResult result = validator.validate(new B());
        Assert.assertTrue(result.isValid());
        Assert.assertNull(result.getDetail());
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
        ValidationResult result = validator.validate("X");
        Assert.assertTrue(result.isValid());
        Assert.assertNull(result.getDetail());
    }


    @Test
    public void test_listNSdValidations() {
        List<ValidatorTypes> validateTypes = ValidatorUtil.listValidations(NSdTestValidator.class);

        Assert.assertEquals(1, validateTypes.size());
        Assert.assertEquals(new QName("http://b", "B"), validateTypes.get(0).getName());
    }

    public static class TestValidator extends BaseValidator {

        public TestValidator() {
            super(JavaTypes.toMessageType(A.class));
        }

        @Override
        public ValidationResult validate(Object obj) {
            if (obj instanceof Object) {
                return validResult();
            } else {
                return invalidResult("not Object");
            }
        }

        @Validator
        public ValidationResult validateB(B b) {
            if (b instanceof B) {
                return validResult();
            } else {
                return invalidResult("b is not B");
            }
        }

        @Validator(name = "X")
        public ValidationResult validateX(String x) {
            if (x.equals("X")) {
                return validResult();
            } else {
                return invalidResult("x is not 'X'");
            }
        }

        @Validator(name = "Z")
        public ValidationResult validateZ(B z) {
            if (z instanceof B) {
                return validResult();
            } else {
                return invalidResult("z is not B");
            }
        }

    }

    public static class NSdTestValidator {

        @Validator(name = "{http://b}B")
        public ValidationResult validateB(B b) {
            if (b instanceof B) {
                return BaseValidator.validResult();
            } else {
                return BaseValidator.invalidResult("b is not B");
            }
        }
    }

    public static class A {

    }

    public static class B {

    }
}
