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
import org.switchyard.transform.ootb.lang.NumberTransforms;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ReaderTransformsTest {

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("Hello SwitchYard", ReaderTransforms.TRANSFORMER.toString(newReader("Hello SwitchYard")));
    }

    @Test
    public void testToInputStream() throws Exception {
        Assert.assertNotNull(ReaderTransforms.TRANSFORMER.toInputStream(newReader("Hello SwitchYard")));
    }

    @Test
    public void testToInputSource() throws Exception {
        Assert.assertNotNull(ReaderTransforms.TRANSFORMER.toInputSource(newReader("Hello SwitchYard")));
    }

    @Test
    public void testToInteger() throws Exception {
        Assert.assertEquals((Integer)1, ReaderTransforms.TRANSFORMER.toInteger(newReader("1")));
    }

    @Test
    public void testToLong() throws Exception {
        Assert.assertEquals((Long)1L, ReaderTransforms.TRANSFORMER.toLong(newReader("1")));
    }

    @Test
    public void testToShort() throws Exception {
        Assert.assertEquals(new Short("1"), ReaderTransforms.TRANSFORMER.toShort(newReader("1")));
    }

    @Test
    public void testToChars() throws Exception {
        Assert.assertEquals("12345", new String(ReaderTransforms.TRANSFORMER.toChars(newReader("12345"))));
    }

    @Test
    public void testToCharacter() throws Exception {
        Assert.assertEquals((Character) '1', ReaderTransforms.TRANSFORMER.toCharacter(newReader("12345")));
    }

    @Test
    public void testToBytes() throws Exception {
        Assert.assertNotNull(new String(ReaderTransforms.TRANSFORMER.toBytes(newReader("12345"))));
    }

    @Test
    public void testToDouble() throws Exception {
        Assert.assertEquals((Double)1D, ReaderTransforms.TRANSFORMER.toDouble(newReader("1")));
    }

    @Test
    public void testToFloat() throws Exception {
        Assert.assertEquals((Float)1F, ReaderTransforms.TRANSFORMER.toFloat(newReader("1")));
    }

    private Reader newReader(String s) {
        return new StringReader(s);
    }
}
