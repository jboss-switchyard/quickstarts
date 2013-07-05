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
package org.switchyard.test;

import java.io.OutputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;

/**
 * Tests the {@link TestDataSource}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class TestDataSourceTest {

    @Test
    public void testDataSource() throws Exception {
        TestDataSource ds = new TestDataSource("name", "text/plain", "data");
        Assert.assertEquals("name", ds.getName());
        Assert.assertEquals("text/plain", ds.getContentType());
        Assert.assertEquals("data", new StringPuller().pull(ds.getInputStream()));
        Assert.assertEquals("data", ds.getDataString());
        ds.setDataString("delta");
        Assert.assertEquals("delta", ds.getDataString());
        OutputStream os = ds.getOutputStream();
        os.write("written".getBytes("UTF-8"));
        os.flush();
        Assert.assertEquals("written", ds.getDataString());
    }

}
