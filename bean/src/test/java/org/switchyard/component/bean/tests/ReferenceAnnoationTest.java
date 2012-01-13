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

package org.switchyard.component.bean.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

public class ReferenceAnnoationTest {
    
    private SwitchYardModel _scannedModel;
    
    @Before
    public void setUp() throws Exception {
        BeanSwitchYardScanner scanner = new BeanSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();
        urls.add(new File("./target/test-classes").toURI().toURL());

        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
        _scannedModel = scanner.scan(input).getModel();
    }

    @Test
    public void testAnnotationRenameService() throws IOException, ClassNotFoundException {
        List<ComponentModel> components = _scannedModel.getComposite().getComponents();
        boolean customReferenceNameFound = false;
        for(ComponentModel component : components) {
            if(component.getName().equals("ServiceWithReference")){
            	for (ComponentReferenceModel reference : component.getReferences()) {
            		if (reference.getName().equals(ServiceWithReferenceBean.RENAMED_REFERENCE)) {
            			customReferenceNameFound = true;
            		}
            	}
            }
        }
        Assert.assertTrue(customReferenceNameFound);
    }

}
