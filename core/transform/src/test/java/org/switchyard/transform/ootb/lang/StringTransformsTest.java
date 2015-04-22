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

package org.switchyard.transform.ootb.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class StringTransformsTest {

    @Test
    public void testToReader() throws Exception {
        Assert.assertNotNull(StringTransforms.TRANSFORMER.toReader("reader"));
    }

    @Test
    public void testToInputStream() throws Exception {
        Assert.assertNotNull(StringTransforms.TRANSFORMER.toInputStream("stream"));
    }

    @Test
    public void testToInputSource() throws Exception {
        Assert.assertNotNull(StringTransforms.TRANSFORMER.toInputSource("source"));
    }

    @Test
    public void testToInteger() throws Exception {
        Assert.assertEquals(new Integer(1), StringTransforms.TRANSFORMER.toInteger("1"));
    }

    @Test
    public void testToLong() throws Exception {
        Assert.assertEquals(new Long(1), StringTransforms.TRANSFORMER.toLong("1"));
    }

    @Test
    public void testToShort() throws Exception {
        Assert.assertNotNull(StringTransforms.TRANSFORMER.toShort("1"));
    }

    @Test
    public void testToChars() throws Exception {
        Assert.assertEquals("ccc", new String(StringTransforms.TRANSFORMER.toChars("ccc")));
    }

    @Test
    public void testToCharacter() throws Exception {
        Assert.assertNotNull(StringTransforms.TRANSFORMER.toCharacter("c"));
    }

    @Test
    public void testToDouble() throws Exception {
        Assert.assertEquals(new Double(1), StringTransforms.TRANSFORMER.toDouble("1"));
    }

    @Test
    public void testToFloat() throws Exception {
        Assert.assertEquals(new Float(1), StringTransforms.TRANSFORMER.toFloat("1"));
    }
}
