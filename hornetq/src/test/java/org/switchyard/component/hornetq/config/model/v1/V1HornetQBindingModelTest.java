/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.hornetq.config.model.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.OperationSelector;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1HornetQConfigModel}.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1HornetQBindingModelTest {
    
    private HornetQBindingModel hbm;

    @Before
    public void parseHornetQBindingModel() throws IOException {
        final ModelPuller<SwitchYardModel> modelPuller = new ModelPuller<SwitchYardModel>();
        final URL xml = V1HornetQConfigModelTest.class.getResource("hornetq-valid-binding.xml");
        final SwitchYardModel switchYardModel = modelPuller.pull(xml);
        hbm = (HornetQBindingModel) switchYardModel.getComposite().getServices().get(0).getBindings().get(0);
    }

    @Test
    public void getHornetQConfig() throws IOException {
        assertThat(hbm.getHornetQConfig(), is(notNullValue()));
        // read the config twice to verify that is works. If I don't store the 
        // config as a field in HornetQBindingModel, trying to call get a second 
        // time results in the model returning null.
        assertThat(hbm.getHornetQConfig(), is(notNullValue()));
    }
    
    @Test
    public void getOperationSelectorName() {
        final OperationSelector operationSelector = hbm.getOperationSelector();
        assertThat(operationSelector.getOperationName(), is(equalTo("printIt")));
    }

}
