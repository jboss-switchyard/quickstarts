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
