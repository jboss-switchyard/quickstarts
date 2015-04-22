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

package org.switchyard.internal.validate;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.ValidationResult;

public class BaseValidatorTest {

    @Test
    public void testGetName_default_with_generics() {
        BaseValidator<String> strv =
            new BaseValidator<String>() {
                public ValidationResult validate(String obj) {
                    if (obj != null) {
                        return validResult();
                    } else {
                        return invalidResult("obj == null");
                    }
                }
        };

        Assert.assertEquals("java:java.lang.String", strv.getName().toString());
    }

    @Test
    public void testGetName_default_without_generics() {
        // No generics...
        BaseValidator strv =
            new BaseValidator() {
                public ValidationResult validate(Object obj) {
                    if (obj != null) {
                        return validResult();
                    } else {
                        return invalidResult("obj == null");
                    }
                }
        };

        Assert.assertEquals("java:java.lang.Object", strv.getName().toString());
    }

    @Test
    public void testGetName_specified_with_generics() {
        final QName name = new QName("string1");

        BaseValidator<String> strv =
            new BaseValidator<String>(name) {
                public ValidationResult validate(String obj) {
                    if (obj != null) {
                        return validResult();
                    } else {
                        return invalidResult("obj == null");
                    }
                }
        };

        Assert.assertEquals(name, strv.getName());
    }

    @Test
    public void testGetName_specified_without_generics() {
        final QName name = new QName("string1");

        BaseValidator strv =
            new BaseValidator(name) {
                public ValidationResult validate(Object obj) {
                    if (obj != null) {
                        return validResult();
                    } else {
                        return invalidResult("obj == null");
                    }
                }
        };

        Assert.assertEquals(name, strv.getName());
    }
    
}
