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

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.TransformSequence;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformSequenceTest {

    @Test
    public void test() {
        final QName A = new QName("a");
        final QName B = new QName("b");
        final QName C = new QName("c");
        final QName D = new QName("d");
        
        BaseTransformerRegistry xformReg = new BaseTransformerRegistry();
        TransformSequence transSequence = TransformSequence.from(A).to(B).to(C).to(D);

        DefaultMessage message = new DefaultMessage().setContent(A);

        // Apply transform sequence ... no relevant transformers in the reg... nothing should
        // happen i.e. content should still be "a"...
        transSequence.apply(message, xformReg);
        Assert.assertEquals(A, message.getContent());

        // Add just th "a" to "b" transformer... run again... should transform to "b", but no further...
        xformReg.addTransformer(new MockTransformer(A, B));
        transSequence.apply(message, xformReg);
        Assert.assertEquals(B, message.getContent());

        // Add the rest of the transforms now... should transform the last steps in one go...
        xformReg.addTransformer(new MockTransformer(B, C)).addTransformer(new MockTransformer(C, D));
        transSequence.apply(message, xformReg);
        Assert.assertEquals(D, message.getContent());
    }

    private class MockTransformer extends BaseTransformer {

        private QName from;
        private QName to;

        private MockTransformer(QName from, QName to) {
            super(from, to);
            this.from = from;
            this.to = to;
        }

        @Override
        public Object transform(Object from) {
            Assert.assertEquals(this.from, from);
            return to;
        }
    }
}
