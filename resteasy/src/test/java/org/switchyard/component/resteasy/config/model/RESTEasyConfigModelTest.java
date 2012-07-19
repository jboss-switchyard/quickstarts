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

package org.switchyard.component.resteasy.config.model;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of rest binding model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyConfigModelTest {

    private static final String REST_BINDING = "rest-binding.xml";
    private static final String REST_BINDING_INVALID = "rest-binding-invalid.xml";

    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<RESTEasyBindingModel> puller = new ModelPuller<RESTEasyBindingModel>();
        RESTEasyBindingModel model = puller.pull(REST_BINDING, getClass());
        Assert.assertTrue(model.isModelValid());
        model = puller.pull(REST_BINDING_INVALID, getClass());
        Assert.assertFalse(model.isModelValid());
    }
}
