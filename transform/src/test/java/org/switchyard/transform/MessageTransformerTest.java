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

package org.switchyard.transform;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.internal.transform.BaseTransformerRegistry;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MessageTransformerTest {
    
    @Test
    public void test() throws IOException {
        final QName A = new QName("a");
        final QName B = new QName("b");

        BaseTransformerRegistry xformReg = new BaseTransformerRegistry();
        TransformSequence transSequence = TransformSequence.from(A).to(B);

        DefaultMessage message = new DefaultMessage().setContent(A);
        Message2MessageTransformer transformer = new Message2MessageTransformer();

        xformReg.addTransformer(transformer, A, B);
        transSequence.apply(message, xformReg);

        Assert.assertEquals(message, transformer.getMessage());
    }

    @Test
    public void testNullOutputFromTransformation() throws IOException {
        final QName A = new QName("a");
        final QName B = new QName("b");

        BaseTransformerRegistry xformReg = new BaseTransformerRegistry();
        TransformSequence transSequence = TransformSequence.from(A).to(B);

        DefaultMessage message = new DefaultMessage().setContent("testNull");
        Message2NullTransformer transformer = new Message2NullTransformer();

        xformReg.addTransformer(transformer, A, B);
        transSequence.apply(message, xformReg);
        System.out.println(message.getContent());
        Assert.assertNull(message.getContent());
    }
}
