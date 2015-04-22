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
