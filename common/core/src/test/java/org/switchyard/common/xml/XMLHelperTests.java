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
