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
package org.switchyard.internal;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
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
        assertScopedPropertyMap(serDeser(buildScopedPropertyMap(), ScopedPropertyMap.class));
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

    private ScopedPropertyMap buildScopedPropertyMap() {
        ScopedPropertyMap spm = new ScopedPropertyMap();
        spm.put(buildContextProperty());
        return spm;
    }

    private DefaultContext buildDefaultContext() {
        return new DefaultContext(buildScopedPropertyMap());
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

    private void assertScopedPropertyMap(ScopedPropertyMap spm) {
        Assert.assertNotNull(spm);
        assertContextProperty((ContextProperty)spm.get().iterator().next());
    }

    private void assertDefaultContext(DefaultContext dc) {
        Assert.assertNotNull(dc);
        assertContextProperty((ContextProperty)dc.getProperties().iterator().next());
    }

    private <T> T serDeser(T obj, Class<T> type) throws Exception {
        FormatType format = FormatType.JSON; //FormatType.XML_BEAN; //FormatType.SER_OBJECT;
        CompressionType compression = null; //CompressionType.ZIP; //CompressionType.GZIP;
        Serializer ser = SerializerFactory.create(format, compression, true);
        byte[] bytes = ser.serialize(obj, type);
        //System.out.println(new String(bytes));
        return ser.deserialize(bytes, type);
    }

}
