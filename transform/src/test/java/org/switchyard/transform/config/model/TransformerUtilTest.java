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

package org.switchyard.transform.config.model;

import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.annotations.Transformer;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.internal.TransformerTypes;
import org.switchyard.transform.internal.TransformerUtil;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformerUtilTest {

    @Test
    public void test_listTransformations() {
        List<TransformerTypes> transformTypes = TransformerUtil.listTransformations(TestTransformer.class);

        Assert.assertEquals(5, transformTypes.size());
        Assert.assertEquals(QName.valueOf("X"), transformTypes.get(0).getFrom());
        Assert.assertEquals(QName.valueOf("Y"), transformTypes.get(0).getTo());
        Assert.assertEquals(QName.valueOf("Z"), transformTypes.get(1).getFrom());
        Assert.assertEquals(JavaTypes.toMessageType(A.class), transformTypes.get(1).getTo());
        Assert.assertEquals(JavaTypes.toMessageType(A.class), transformTypes.get(2).getFrom());
        Assert.assertEquals(JavaTypes.toMessageType(B.class), transformTypes.get(2).getTo());
        Assert.assertEquals(JavaTypes.toMessageType(B.class), transformTypes.get(3).getFrom());
        Assert.assertEquals(QName.valueOf("Z"), transformTypes.get(3).getTo());
        Assert.assertEquals(JavaTypes.toMessageType(B.class), transformTypes.get(4).getFrom());
        Assert.assertEquals(JavaTypes.toMessageType(A.class), transformTypes.get(4).getTo());
    }

    @Test
    public void test_transform_interface_impl() {
        org.switchyard.transform.Transformer transformer = TransformerUtil.newTransformer(TestTransformer.class, JavaTypes.toMessageType(A.class), JavaTypes.toMessageType(B.class));

        Assert.assertTrue(transformer instanceof TestTransformer);
        Assert.assertTrue(transformer.transform(new A()) instanceof B);
    }

    @Test
    public void test_transform_anno_no_types_defined() {
        org.switchyard.transform.Transformer transformer = TransformerUtil.newTransformer(TestTransformer.class, JavaTypes.toMessageType(B.class), JavaTypes.toMessageType(A.class));

        Assert.assertTrue(!(transformer instanceof TestTransformer));
        Assert.assertTrue(transformer.transform(new B()) instanceof A);
    }

    @Test
    public void test_transform_unknown() {
        try {
            TransformerUtil.newTransformer(TestTransformer.class, QName.valueOf("AAA"), QName.valueOf("BBB"));
            Assert.fail("Expected Exception");
        } catch(RuntimeException e) {
            Assert.assertEquals("Error constructing Transformer instance for class 'org.switchyard.transform.config.model.TransformerUtilTest$TestTransformer'.  " +
                    "Class does not support a transformation from type 'AAA' to type 'BBB'.",
                    e.getMessage());
        }
    }

    @Test
    public void test_transform_anno_types_defined() {
        org.switchyard.transform.Transformer transformer = TransformerUtil.newTransformer(TestTransformer.class, QName.valueOf("X"), QName.valueOf("Y"));

        Assert.assertTrue(!(transformer instanceof TestTransformer));
        Assert.assertEquals("Y", transformer.transform("X"));
    }


    @Test
    public void test_listNSdTransformations() {
        List<TransformerTypes> transformTypes = TransformerUtil.listTransformations(NSdTestTransformer.class);

        Assert.assertEquals(1, transformTypes.size());
        Assert.assertEquals(new QName("http://b", "B"), transformTypes.get(0).getFrom());
        Assert.assertEquals(new QName("http://a", "A"), transformTypes.get(0).getTo());
    }

    public static class TestTransformer extends BaseTransformer {

        public TestTransformer() {
            super(JavaTypes.toMessageType(A.class), JavaTypes.toMessageType(B.class));
        }

        // A to B
        @Override
        public Object transform(Object from) {
            return new B();
        }

        // B to A
        @Transformer
        public A bToA(B b) {
            return new A();
        }

        // X to Y
        @Transformer(from = "X", to = "Y")
        public String xToY(String x) {
            return "Y";
        }

        // Z to A
        @Transformer(from = "Z")
        public A zToA(B z) {
            return new A();
        }

        // B to Z
        @Transformer(to = "Z")
        public A bToZ(B b) {
            return new A();
        }
    }

    public static class NSdTestTransformer {

        @Transformer(from = "{http://b}B", to = "{http://a}A")
        public A bToA(B b) {
            return new A();
        }
    }

    public static class A {

    }

    public static class B {

    }
}
