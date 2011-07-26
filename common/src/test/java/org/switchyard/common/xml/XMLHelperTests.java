/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
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
package org.switchyard.common.xml;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;

/**
 * XMLHelperTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class XMLHelperTests {

    @Test
    public void testSplitQNames() throws Exception {
        QName[] q = XMLHelper.splitQNames("foo/bar");
        Assert.assertArrayEquals(new QName[]{new QName("foo"), new QName("bar")}, q);
        q = XMLHelper.splitQNames("foo/ ");
        Assert.assertArrayEquals(new QName[]{new QName("foo")}, q);
        q = XMLHelper.splitQNames("{http://foo.com}foo/{http://bar.com}bar");
        Assert.assertArrayEquals(new QName[]{QName.valueOf("{http://foo.com}foo"), QName.valueOf("{http://bar.com}bar")}, q);
        q = XMLHelper.splitQNames("{http://foo.com/f}foo/{http://bar.com/b}bar");
        Assert.assertArrayEquals(new QName[]{QName.valueOf("{http://foo.com/f}foo"), QName.valueOf("{http://bar.com/b}bar")}, q);
    }

}
