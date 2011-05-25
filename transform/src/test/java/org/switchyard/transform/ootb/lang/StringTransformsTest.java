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
