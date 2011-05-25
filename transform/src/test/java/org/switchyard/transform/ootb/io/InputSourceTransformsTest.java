/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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
