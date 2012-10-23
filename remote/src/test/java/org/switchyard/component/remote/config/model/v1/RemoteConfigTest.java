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
package org.switchyard.component.remote.config.model.v1;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

public class RemoteConfigTest {

    @Test
    public void readConfig() throws Exception {
        InputStream in = Classes.getResourceAsStream("remote-binding.xml", getClass());
        SwitchYardModel model = new ModelPuller<SwitchYardModel>().pull(in);
        Assert.assertTrue(model.isModelValid());
        List<CompositeServiceModel> services = model.getComposite().getServices();
        CompositeServiceModel compositeServiceModel = services.get(0);
        List<BindingModel> bindings = compositeServiceModel.getBindings();
        Assert.assertEquals(1, bindings.size());
        Assert.assertEquals("remote", bindings.get(0).getType());
    }
}
