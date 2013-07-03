/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

