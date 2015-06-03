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

package org.switchyard.validate;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;

public class BaseValidatorTest {

    @Test
    public void testTypedValidator() throws Exception {
        Validator<?> t = new StringValidator();
        Assert.assertEquals(String.class, t.getType());
    }
    
    @Test
    public void testUntypedValidator() throws Exception {
        Validator<?> t = new UntypedValidator();
        Assert.assertEquals(Object.class, t.getType());
    }
    
    @Test
    public void testImplementsValidator() throws Exception {
        Validator<?> t = new ImplementsValidator();
        Assert.assertEquals(String.class, t.getType());
    }
}

class StringValidator extends BaseValidator<String> {
    public ValidationResult validate(String num) {
        return new ValidationResult() {
            public boolean isValid() {
                return false;
            }
            public String getDetail() {
                return "error";
            }
        };
    }
}

class UntypedValidator extends BaseValidator {
    public ValidationResult validate(Object obj) {
        return new ValidationResult() {
            public boolean isValid() {
                return false;
            }
            public String getDetail() {
                return "error";
            }
        };
    }
}

class ImplementsValidator implements Validator {

    @Override
    public QName getName() {
        return null;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public Validator setName(QName name) {
        return null;
    }

    @Override
    public ValidationResult validate(Object name) {
        return new ValidationResult() {
            public boolean isValid() {
                return false;
            }
            public String getDetail() {
                return "error";
            }
        };
    }
    
}

