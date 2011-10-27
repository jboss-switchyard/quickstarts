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

package org.switchyard.validate.config.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.config.model.validators.AValidator;
import org.switchyard.validate.config.model.validators.BValidator;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidateSwitchYardScannerTest {

    @Test
    public void test() throws IOException {
        ValidateSwitchYardScanner scanner = new ValidateSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the validate module !!
        urls.add(new File("./target/test-classes").toURI().toURL());

        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
        SwitchYardModel switchyard = scanner.scan(input).getModel();
        List<ValidateModel> models = switchyard.getValidates().getValidates();

        Assert.assertEquals(8, models.size());
        assertModelInstanceOK((JavaValidateModel) models.get(0));
        assertModelInstanceOK((JavaValidateModel) models.get(1));
    }

    private void assertModelInstanceOK(JavaValidateModel model) {
        if (model.getName().toString().equals("{urn:switchyard-validate:test-validators:1.0}a")) {
            Assert.assertEquals(AValidator.class.getName(), model.getClazz());
        } else if (model.getName().toString().equals("{urn:switchyard-validate:test-validators:1.0}b")) {
            Assert.assertEquals(BValidator.class.getName(), model.getClazz());
        }
    }
}
