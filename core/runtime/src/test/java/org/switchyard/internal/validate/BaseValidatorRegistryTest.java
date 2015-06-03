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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.Validator;
import org.switchyard.validate.ValidatorRegistry;

public class BaseValidatorRegistryTest {
    
    private ValidatorRegistry _registry;
    
    @Before
    public void setUp() throws Exception {
        _registry = new BaseValidatorRegistry();
    }
    
    @Test
    public void testNullValidator() {
        final QName name = new QName("notfound");
        
         Validator<?> validator = _registry.getValidator(name);
         Assert.assertTrue(validator == null);
    }
    
    @Test
    public void testAddGetValidator() {
        final QName name = new QName("a");
        
        BaseValidator<String> t = 
            new BaseValidator<String>(name) {
                public ValidationResult validate(String obj) {
                    if (obj != null) {
                        return validResult();
                    } else {
                        return invalidResult("obj == null");
                    }
                }
        };
        
        _registry.addValidator(t);
        Assert.assertEquals(t, _registry.getValidator(name));      
    }

    @Test
    public void test_fallbackValidatorComparator_resolvable() {
        List<BaseValidatorRegistry.JavaSourceFallbackValidator> validatorsList = new ArrayList<BaseValidatorRegistry.JavaSourceFallbackValidator>();

        // Mix them up when inserting
        validatorsList.add(new BaseValidatorRegistry.JavaSourceFallbackValidator(C.class, null));
        validatorsList.add(new BaseValidatorRegistry.JavaSourceFallbackValidator(E.class, null));
        validatorsList.add(new BaseValidatorRegistry.JavaSourceFallbackValidator(D.class, null));
        validatorsList.add(new BaseValidatorRegistry.JavaSourceFallbackValidator(A.class, null));
        validatorsList.add(new BaseValidatorRegistry.JavaSourceFallbackValidator(B.class, null));

        BaseValidatorRegistry.JavaSourceFallbackValidatorComparator comparator = new BaseValidatorRegistry.JavaSourceFallbackValidatorComparator();
        Collections.sort(validatorsList, comparator);

        // Should be sorted, sub-types first...
        Assert.assertTrue(validatorsList.get(0).getJavaType() == E.class);
        Assert.assertTrue(validatorsList.get(1).getJavaType() == D.class);
        Assert.assertTrue(validatorsList.get(2).getJavaType() == C.class);
        Assert.assertTrue(validatorsList.get(3).getJavaType() == B.class);
        Assert.assertTrue(validatorsList.get(4).getJavaType() == A.class);
    }

    @Test
    public void test_getFallbackValidator_resolvable() {
        addValidator(B.class);
        addValidator(C.class);

        Validator<?> validator;

        // Should return no validator...
        validator = _registry.getValidator(getType(A.class));
        Assert.assertNull(validator);

        // Should return the C validator...
        validator = _registry.getValidator(getType(D.class));
        Assert.assertNotNull(validator);
        Assert.assertEquals(getType(C.class), validator.getName());

    }

    private void addValidator(Class<?> type) {
        QName name = getType(type);
        _registry.addValidator(new TestValidator(name));
    }

    private QName getType(Class<?> type) {
        return JavaTypes.toMessageType(type);
    }

    public class A {}
    public class B extends A {}
    public class C extends B {}
    public class D extends C implements I {}
    public class E extends D {}
    public interface I {}

    public class TestValidator extends BaseValidator {
        public TestValidator(QName name) {
            super(name);
        }

        @Override
        public ValidationResult validate(Object obj) {
            if (obj != null) {
                return validResult();
            } else {
                return invalidResult("obj != null");
            }
        }
    }
    
}
