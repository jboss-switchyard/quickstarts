/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
