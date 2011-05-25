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
