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
import org.switchyard.transform.ootb.lang.StringTransforms;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InputSourceTransformsTest {

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("Hello SwitchYard", InputSourceTransforms.TRANSFORMER.toString(newInputSource("Hello SwitchYard")));
    }

    @Test
    public void testToReader() throws Exception {
        Assert.assertNotNull(InputSourceTransforms.TRANSFORMER.toReader(newInputSource("Hello SwitchYard")));
    }

    @Test
    public void testToInputStream() throws Exception {
        Assert.assertNotNull(InputSourceTransforms.TRANSFORMER.toInputStream(newInputSource("Hello SwitchYard")));
    }

    @Test
    public void testToInteger() throws Exception {
        Assert.assertEquals((Integer)1, InputSourceTransforms.TRANSFORMER.toInteger(newInputSource("1")));
    }

    @Test
    public void testToLong() throws Exception {
        Assert.assertEquals((Long)1L, InputSourceTransforms.TRANSFORMER.toLong(newInputSource("1")));
    }

    @Test
    public void testToShort() throws Exception {
        Assert.assertEquals(new Short("1"), InputSourceTransforms.TRANSFORMER.toShort(newInputSource("1")));
    }

    @Test
    public void testToChars() throws Exception {
        Assert.assertEquals("12345", new String(InputSourceTransforms.TRANSFORMER.toChars(newInputSource("12345"))));
    }

    @Test
    public void testToCharacter() throws Exception {
        Assert.assertEquals((Character) '1', InputSourceTransforms.TRANSFORMER.toCharacter(newInputSource("12345")));
    }

    @Test
    public void testToBytes() throws Exception {
        Assert.assertNotNull(new String(InputSourceTransforms.TRANSFORMER.toBytes(newInputSource("12345"))));
    }

    @Test
    public void testToDouble() throws Exception {
        Assert.assertEquals((Double)1D, InputSourceTransforms.TRANSFORMER.toDouble(newInputSource("1")));
    }

    @Test
    public void testToFloat() throws Exception {
        Assert.assertEquals((Float)1F, InputSourceTransforms.TRANSFORMER.toFloat(newInputSource("1")));
    }

    private InputSource newInputSource(String s) {
        return StringTransforms.TRANSFORMER.toInputSource(s);
    }
}
