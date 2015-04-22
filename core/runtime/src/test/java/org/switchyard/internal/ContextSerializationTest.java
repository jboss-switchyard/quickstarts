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
package org.switchyard.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.serial.CompressionType;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;

/**
 * Tests de/serialization of a Context.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ContextSerializationTest {

    @Test
    public void testContextProperty() throws Exception {
        assertContextProperty(serDeser(buildContextProperty(), ContextProperty.class));
    }

    @Test
    public void testScopedPropertyMap() throws Exception {
        assertScopedPropertyMap(serDeser(buildScopedPropertyMap(), Map.class));
    }

    @Test
    public void testDefaultContext() throws Exception {
        assertDefaultContext(serDeser(buildDefaultContext(), DefaultContext.class));
    }

    private ContextProperty buildContextProperty() {
        ContextProperty cp = new ContextProperty("foo", Scope.EXCHANGE, "bar");
        cp.addLabels("baz", "whiz");
        return cp;
    }

    private Map<String,Property> buildScopedPropertyMap() {
        Map<String,Property> properties = new HashMap<String, Property>();
        ContextProperty property = buildContextProperty();
        properties.put(property.getName(), property);
        return properties;
    }

    private DefaultContext buildDefaultContext() {
        return new DefaultContext(Scope.EXCHANGE, buildScopedPropertyMap());
    }

    private void assertContextProperty(ContextProperty cp) {
        Assert.assertNotNull(cp);
        Assert.assertEquals("foo", cp.getName());
        Assert.assertSame(Scope.EXCHANGE, cp.getScope());
        Assert.assertEquals("bar", cp.getValue());
        Set<String> labels = cp.getLabels();
        Assert.assertEquals(2, labels.size());
        Assert.assertTrue(labels.contains("baz"));
        Assert.assertTrue(labels.contains("whiz"));
        Assert.assertFalse(labels.contains("beep"));
    }

    private void assertScopedPropertyMap(Map<String, Property> spm) {
        Assert.assertNotNull(spm);
        assertContextProperty((ContextProperty) spm.values().iterator().next());
    }

    private void assertDefaultContext(DefaultContext dc) {
        Assert.assertNotNull(dc);
        assertContextProperty((ContextProperty)dc.getProperties().iterator().next());
    }

    private <T> T serDeser(T obj, Class<T> type) throws Exception {
        FormatType format = FormatType.JSON; //FormatType.XML_BEAN; //FormatType.SER_OBJECT;
        CompressionType compression = null; //CompressionType.ZIP; //CompressionType.GZIP;
        Serializer ser = SerializerFactory.create(format, compression, true);
        //ser.setPrettyPrint(true);
        byte[] bytes = ser.serialize(obj, type);
        //System.out.println(new String(bytes));
        return ser.deserialize(bytes, type);
    }

}
