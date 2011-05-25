/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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

package org.switchyard.transform.ootb;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.TransformerRegistryLoader;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class OutOfTheBoxTransformersLoadingTest {

    private BaseTransformerRegistry registry;

    public OutOfTheBoxTransformersLoadingTest() {
        registry = new BaseTransformerRegistry();
        new TransformerRegistryLoader(registry).loadOOTBTransforms();
    }

    @Test
    public void test_DOM_Transforms_Loaded() {
        Assert.assertNotNull(registry.getTransformer(qName(String.class), qName(Element.class)));
        Assert.assertNotNull(registry.getTransformer(qName(Element.class), qName(char[].class)));
    }

    @Test
    public void test_String_Transforms_Loaded() {
        Assert.assertNotNull(registry.getTransformer(qName(String.class), qName(Reader.class)));
        Assert.assertNotNull(registry.getTransformer(qName(String.class), qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(String.class), qName(char[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(String.class), qName(InputSource.class)));
    }

    @Test
    public void test_Number_Transforms_Loaded() {
        Assert.assertNotNull(registry.getTransformer(qName(Number.class), qName(Reader.class)));
        Assert.assertNotNull(registry.getTransformer(qName(Number.class), qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Number.class), qName(char[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Number.class), qName(InputSource.class)));

        Assert.assertNotNull(registry.getTransformer(qName(Integer.class), qName(Reader.class)));
        Assert.assertNotNull(registry.getTransformer(qName(Integer.class), qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Integer.class), qName(char[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Integer.class), qName(InputSource.class)));

        Assert.assertNotNull(registry.getTransformer(qName(Double.class), qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(Double.class), qName(Reader.class)));
        Assert.assertNotNull(registry.getTransformer(qName(Double.class), qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Double.class), qName(char[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Double.class), qName(InputSource.class)));
    }

    @Test
    public void test_Reader_Transforms_Loaded() {
        Assert.assertNotNull(registry.getTransformer(qName(Reader.class),            qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(InputStreamReader.class), qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(Reader.class),            qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(FileReader.class),        qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Reader.class),            qName(char[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(Reader.class),            qName(InputSource.class)));
        Assert.assertNotNull(registry.getTransformer(qName(FileReader.class),        qName(InputSource.class)));
        Assert.assertNotNull(registry.getTransformer(qName(StringReader.class),      qName(InputSource.class)));
    }

    @Test
    public void test_InputStream_Transforms_Loaded() {
        Assert.assertNotNull(registry.getTransformer(qName(InputStream.class),       qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(FilterInputStream.class), qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(InputStream.class),       qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(FileInputStream.class),   qName(byte[].class)));
    }

    @Test
    public void test_InputSource_Transforms_Loaded() {
        Assert.assertNotNull(registry.getTransformer(qName(InputSource.class), qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(InputSource.class), qName(String.class)));
        Assert.assertNotNull(registry.getTransformer(qName(InputSource.class), qName(byte[].class)));
        Assert.assertNotNull(registry.getTransformer(qName(InputSource.class), qName(byte[].class)));
    }

    private QName qName(Class<?> aClass) {
        return JavaService.toMessageType(aClass);
    }
}
