/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.internal.transform;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformSequenceTest {

    @Test
    public void test() {
        BaseTransformerRegistry xformReg = new BaseTransformerRegistry();
        TransformSequence transSequence = TransformSequence.from("a").to("b").to("c").to("d");

        DefaultMessage message = new DefaultMessage().setContent("a");

        // Apply transform sequence ... no relevant transformers in the reg... nothing should
        // happen i.e. content should still be "a"...
        transSequence.apply(message, xformReg);
        Assert.assertEquals("a", message.getContent());

        // Add just th "a" to "b" transformer... run again... should transform to "b", but no further...
        xformReg.addTransformer(new MockTransformer("a", "b"));
        transSequence.apply(message, xformReg);
        Assert.assertEquals("b", message.getContent());

        // Add the rest of the transforms now... should transform the last steps in one go...
        xformReg.addTransformer(new MockTransformer("b", "c")).addTransformer(new MockTransformer("c", "d"));
        transSequence.apply(message, xformReg);
        Assert.assertEquals("d", message.getContent());
    }

    private class MockTransformer extends BaseTransformer {

        private String from;
        private String to;

        private MockTransformer(String from, String to) {
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
