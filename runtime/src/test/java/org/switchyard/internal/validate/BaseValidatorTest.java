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

package org.switchyard.internal.validate;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.validate.BaseValidator;

public class BaseValidatorTest {

    @Test
    public void testGetName_default_with_generics() {
        BaseValidator<String> strv =
            new BaseValidator<String>() {
                public boolean validate(String obj) {
                    return obj != null;
                }
        };

        Assert.assertEquals("java:java.lang.String", strv.getName().toString());
    }

    @Test
    public void testGetName_default_without_generics() {
        // No generics...
        BaseValidator strv =
            new BaseValidator() {
                public boolean validate(Object obj) {
                    return obj != null;
                }
        };

        Assert.assertEquals("java:java.lang.Object", strv.getName().toString());
    }

    @Test
    public void testGetName_specified_with_generics() {
        final QName name = new QName("string1");

        BaseValidator<String> strv =
            new BaseValidator<String>(name) {
                public boolean validate(String obj) {
                    return obj != null;
                }
        };

        Assert.assertEquals(name, strv.getName());
    }

    @Test
    public void testGetName_specified_without_generics() {
        final QName name = new QName("string1");

        BaseValidator strv =
            new BaseValidator(name) {
                public boolean validate(Object obj) {
                    return obj != null;
                }
        };

        Assert.assertEquals(name, strv.getName());
    }
}
