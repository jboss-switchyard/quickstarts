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

package org.switchyard.transform.dozer.internal;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.transform.AbstractTransformerTestCase;
import org.switchyard.transform.Transformer;

/**
 * Dozer transformer tests.
 */
public class DozerTransformerTest extends AbstractTransformerTestCase {

    @Test
    public void test_no_mapping_file() throws IOException {
        Transformer<?,?> transformer = getTransformer("sw-config-nofile.xml");
        Assert.assertTrue(transformer instanceof DozerTransformer);
        DozerTransformer dozer = DozerTransformer.class.cast(transformer);
        ClassA a = new ClassA();
        a.setFieldA("testa");
        a.setFieldB("testb");
        
        Object result = dozer.transform(a);
        Assert.assertTrue(result instanceof ClassB);
        ClassB b = ClassB.class.cast(result);
        Assert.assertEquals("testa", b.getFieldA());
        Assert.assertNull(b.getFieldB_mod());
    }

    @Test
    public void test_mapping_file() throws IOException {
        Transformer<?,?> transformer = getTransformer("sw-config-file.xml");
        Assert.assertTrue(transformer instanceof DozerTransformer);
        DozerTransformer dozer = DozerTransformer.class.cast(transformer);
        ClassA a = new ClassA();
        a.setFieldA("testa");
        a.setFieldB("testb");
        Object result = dozer.transform(a);
        Assert.assertTrue(result instanceof ClassB);
        ClassB b = ClassB.class.cast(result);
        Assert.assertEquals("testa", b.getFieldA());
        Assert.assertEquals("testb", b.getFieldB_mod());
    }

    @Test
    public void test_multi_mapping_file() throws IOException {
        Transformer<?,?> transformer = getTransformer("sw-config-multi-file.xml");
        Assert.assertTrue(transformer instanceof DozerTransformer);
        DozerTransformer dozer = DozerTransformer.class.cast(transformer);
        ClassA a = new ClassA();
        a.setFieldA("testa");
        a.setFieldB("testb");
        ClassC c = new ClassC();
        c.setFieldA(a);
        c.setFieldC("testc");
        
        Object result = dozer.transform(c);
        Assert.assertTrue(result instanceof ClassD);
        ClassD d = ClassD.class.cast(result);
        ClassB b = d.getFieldB();
        Assert.assertEquals("testa", b.getFieldA());
        Assert.assertEquals("testb", b.getFieldB_mod());
        Assert.assertEquals("testc", d.getFieldD());
    }
}
