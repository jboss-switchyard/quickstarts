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
