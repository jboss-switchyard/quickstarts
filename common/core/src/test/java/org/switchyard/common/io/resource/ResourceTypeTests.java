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
    public void testMultipleExtensions() throws Exception {
        ResourceType.install("JPG", null, ".jpg", ".jpeg");
        ResourceType jpg = ResourceType.forExtension(".jpg");
        ResourceType jpeg = ResourceType.forExtension(".jpeg");
        Assert.assertSame(jpg, jpeg);
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
        Assert.assertArrayEquals(new ResourceType[]{a}, ResourceType.forExtension(".a", false));
        Assert.assertArrayEquals(new ResourceType[]{a, ab, abcd}, ResourceType.forExtension(".a", true));
        Assert.assertArrayEquals(new ResourceType[]{a}, ResourceType.forLocation("path/to/foo.a", false));
        Assert.assertArrayEquals(new ResourceType[]{a, ab, abcd}, ResourceType.forLocation("path/to/foo.a", true));
    }

}
