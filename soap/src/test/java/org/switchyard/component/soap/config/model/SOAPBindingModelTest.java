/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.soap.config.model;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of SOAP binding model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class SOAPBindingModelTest {

    private static final String SOAP_BINDING = "soap-binding-reference.xml";

    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<SOAPBindingModel> puller = new ModelPuller<SOAPBindingModel>();
        SOAPBindingModel model = puller.pull(SOAP_BINDING, getClass());
        Assert.assertTrue(model.isModelValid());
        Assert.assertEquals("Missing endpoint address", model.getEndpointAddress(), "http://modified.com/phantom");
    }
}
