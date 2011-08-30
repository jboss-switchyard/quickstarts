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
package org.switchyard.common.io.resource;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * ResourceTypeTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ResourceTypeTests {

    @Test
    public void testBasicType() throws Exception {
        ResourceType txt1 = ResourceType.valueOf("TXT");
        Assert.assertEquals("Text" , txt1.getDescription());
        Set<String> exts = txt1.getExtensions();
        Assert.assertEquals(1, exts.size());
        Assert.assertEquals(".txt", exts.iterator().next());
        ResourceType txt2 = ResourceType.forName("TXT");
        Assert.assertSame(txt1, txt2);
        ResourceType txt3 = ResourceType.forExtension(".txt");
        Assert.assertSame(txt1, txt3);
        ResourceType txt4 = ResourceType.forLocation("foo/bar.txt");
        Assert.assertSame(txt1, txt4);
    }

    @Test
    public void testOOTBInheritance() throws Exception {
        ResourceType dtable = ResourceType.valueOf("DTABLE");
        Assert.assertEquals(2, dtable.getExtensions().size());
        Assert.assertEquals(2, dtable.getExtensions(true).size());
        Assert.assertEquals(0, dtable.getExtensions(false).size());
    }

    @Test
    @SuppressWarnings("unused")
    public void testCustomInheritance() throws Exception {
        ResourceType a = ResourceType.install("A", null, ".a");
        ResourceType b = ResourceType.install("B", null, ".b");
        ResourceType ab = ResourceType.install("AB", null, "{A}", "{B}");
        ResourceType c = ResourceType.install("C", null, ".c");
        ResourceType abcd = ResourceType.install("ABCD", null, "{AB}", "{C}", ".d");
        Assert.assertEquals(4, abcd.getExtensions().size());
        Assert.assertEquals(4, abcd.getExtensions(true).size());
        Assert.assertEquals(1, abcd.getExtensions(false).size());
    }

}
