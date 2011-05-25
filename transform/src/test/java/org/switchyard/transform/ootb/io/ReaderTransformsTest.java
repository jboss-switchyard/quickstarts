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
