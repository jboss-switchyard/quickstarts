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

package org.switchyard.internal.transform;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.transform.BaseTransformer;

public class BaseTransformerTest {

    @Test
    public void testGetName_default_with_generics() {
        BaseTransformer<Double, String> intToStr =
            new BaseTransformer<Double, String>() {
                public String transform(Double from) {
                    return null;
                }
        };

        Assert.assertEquals("java:java.lang.Double", intToStr.getFrom().toString());
        Assert.assertEquals("java:java.lang.String", intToStr.getTo().toString());
    }

    @Test
    public void testGetName_default_without_generics() {
        // No generics...
        BaseTransformer intToStr =
            new BaseTransformer() {
                public Object transform(Object from) {
                    return null;
                }
        };

        Assert.assertEquals("java:java.lang.Object", intToStr.getFrom().toString());
        Assert.assertEquals("java:java.lang.Object", intToStr.getTo().toString());
    }

    @Test
    public void testGetName_specified_with_generics() {
        final QName fromName = new QName("string1");
        final QName toName = new QName("string2");

        BaseTransformer<String, String> intToStr =
            new BaseTransformer<String, String>(fromName, toName) {
                public String transform(String from) {
                    return null;
                }
        };

        Assert.assertEquals(fromName, intToStr.getFrom());
        Assert.assertEquals(toName, intToStr.getTo());
    }

    @Test
    public void testGetName_specified_without_generics() {
        final QName fromName = new QName("string1");
        final QName toName = new QName("string2");

        BaseTransformer intToStr =
            new BaseTransformer(fromName, toName) {
                public Object transform(Object from) {
                    return null;
                }
        };

        Assert.assertEquals(fromName, intToStr.getFrom());
        Assert.assertEquals(toName, intToStr.getTo());
    }
}
