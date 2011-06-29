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

package org.switchyard.component.bean.jaxbautoregister;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.jaxb.internal.JAXBMarshalTransformer;
import org.switchyard.transform.jaxb.internal.JAXBUnmarshalTransformer;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@SwitchYardTestCaseConfig(config = "/jaxbautoregister/switchyard-config-01.xml", mixins = CDIMixIn.class)
public class JAXBAutoRegisterTest extends SwitchYardTestCase {

    @Test
    public void test_userOverride() {
        TransformerRegistry transformRegistry = getServiceDomain().getTransformerRegistry();

        Transformer<?,?> unmarshaller = transformRegistry.getTransformer(new QName("purchaseOrder"), JavaService.toMessageType(POType.class));
        Transformer<?,?> marshaller   = transformRegistry.getTransformer(JavaService.toMessageType(POType.class), new QName("purchaseOrder"));

        Assert.assertTrue(unmarshaller instanceof JAXBUnmarshalTransformer);
        Assert.assertEquals("purchaseOrder", unmarshaller.getFrom().toString());
        Assert.assertEquals(JavaService.toMessageType(POType.class), unmarshaller.getTo());

        Assert.assertTrue(marshaller instanceof JAXBMarshalTransformer);
        Assert.assertEquals(JavaService.toMessageType(POType.class), marshaller.getFrom());
        Assert.assertEquals("purchaseOrder", marshaller.getTo().toString());
    }
}
