/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.http.config.model;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of HTTP binding model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpConfigModelTest {

    private static final String HTTP_BINDING = "http-binding.xml";
    private static final String HTTP_BINDING2 = "http-binding2.xml";
    private static final String HTTP_BINDING_INVALID = "http-binding-invalid.xml";

    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<HttpBindingModel> puller = new ModelPuller<HttpBindingModel>();
        HttpBindingModel model = puller.pull(HTTP_BINDING, getClass());
        Assert.assertTrue(model.isModelValid());
        model = puller.pull(HTTP_BINDING2, getClass());
        Assert.assertTrue(model.isModelValid());
        model = puller.pull(HTTP_BINDING_INVALID, getClass());
        Assert.assertFalse(model.isModelValid());
    }
}
