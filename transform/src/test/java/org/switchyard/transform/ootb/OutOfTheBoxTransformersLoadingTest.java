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

package org.switchyard.transform.ootb;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.transform.internal.TransformerRegistryLoader;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

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
        return JavaTypes.toMessageType(aClass);
    }
}
