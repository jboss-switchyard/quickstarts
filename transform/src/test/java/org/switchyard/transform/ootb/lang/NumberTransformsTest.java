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
public class NumberTransformsTest {

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("1", new NumberTransforms().toString(1));
        Assert.assertEquals("1", new NumberTransforms().toString(1L));
        Assert.assertEquals("1.0", new NumberTransforms().toString(1D));
    }

    @Test
    public void testToReader() throws Exception {
        Assert.assertNotNull(NumberTransforms.TRANSFORMER.toReader(1D));
    }

    @Test
    public void testToInputStream() throws Exception {
        Assert.assertNotNull(NumberTransforms.TRANSFORMER.toInputStream(1D));
    }

    @Test
    public void testToInputSource() throws Exception {
        Assert.assertNotNull(NumberTransforms.TRANSFORMER.toInputSource(1D));
    }

    @Test
    public void testToChars() throws Exception {
        Assert.assertEquals("12345.0", new String(NumberTransforms.TRANSFORMER.toChars(12345D)));
    }

    @Test
    public void testToCharacter() throws Exception {
        Assert.assertEquals((Character)'1', NumberTransforms.TRANSFORMER.toCharacter(12345D));
    }

    @Test
    public void testToBytes() throws Exception {
        Assert.assertEquals("12345.0", new String(NumberTransforms.TRANSFORMER.toBytes(12345D)));
    }
}
