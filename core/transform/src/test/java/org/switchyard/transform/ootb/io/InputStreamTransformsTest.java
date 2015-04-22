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

package org.switchyard.transform.ootb.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InputStreamTransformsTest {

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("Hello SwitchYard", InputStreamTransforms.TRANSFORMER.toString(newInputStream("Hello SwitchYard")));
    }

    @Test
    public void testToReader() throws Exception {
        Assert.assertNotNull(InputStreamTransforms.TRANSFORMER.toReader(newInputStream("Hello SwitchYard")));
    }

    @Test
    public void testToInputSource() throws Exception {
        Assert.assertNotNull(InputStreamTransforms.TRANSFORMER.toInputSource(newInputStream("Hello SwitchYard")));
    }

    @Test
    public void testToInteger() throws Exception {
        Assert.assertEquals((Integer)1, InputStreamTransforms.TRANSFORMER.toInteger(newInputStream("1")));
    }

    @Test
    public void testToLong() throws Exception {
        Assert.assertEquals((Long)1L, InputStreamTransforms.TRANSFORMER.toLong(newInputStream("1")));
    }

    @Test
    public void testToShort() throws Exception {
        Assert.assertEquals(new Short("1"), InputStreamTransforms.TRANSFORMER.toShort(newInputStream("1")));
    }

    @Test
    public void testToChars() throws Exception {
        Assert.assertEquals("12345", new String(InputStreamTransforms.TRANSFORMER.toChars(newInputStream("12345"))));
    }

    @Test
    public void testToCharacter() throws Exception {
        Assert.assertEquals((Character) '1', InputStreamTransforms.TRANSFORMER.toCharacter(newInputStream("12345")));
    }

    @Test
    public void testToBytes() throws Exception {
        Assert.assertNotNull(new String(InputStreamTransforms.TRANSFORMER.toBytes(newInputStream("12345"))));
    }

    @Test
    public void testToDouble() throws Exception {
        Assert.assertEquals((Double)1D, InputStreamTransforms.TRANSFORMER.toDouble(newInputStream("1")));
    }

    @Test
    public void testToFloat() throws Exception {
        Assert.assertEquals((Float)1F, InputStreamTransforms.TRANSFORMER.toFloat(newInputStream("1")));
    }

    private InputStream newInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }
}
