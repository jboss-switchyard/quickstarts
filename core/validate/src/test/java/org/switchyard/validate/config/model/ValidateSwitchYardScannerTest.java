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

package org.switchyard.validate.config.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.config.model.validators.AValidator;
import org.switchyard.validate.config.model.validators.BValidator;
import org.switchyard.validate.config.model.validators.BeanValidator;

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

        Assert.assertEquals(9, models.size());
        assertModelInstanceOK((JavaValidateModel) models.get(0));
        assertModelInstanceOK((JavaValidateModel) models.get(1));
        assertModelInstanceOK((JavaValidateModel) models.get(2));
    }

    private void assertModelInstanceOK(JavaValidateModel model) {
        if (model.getName().toString().equals("{urn:switchyard-validate:test-validators:1.0}a")) {
            Assert.assertEquals(AValidator.class.getName(), model.getClazz());
        } else if (model.getName().toString().equals("{urn:switchyard-validate:test-validators:1.0}b")) {
            Assert.assertEquals(BValidator.class.getName(), model.getClazz());
        } else if (model.getName().toString().equals("{urn:switchyard-validate:test-validators:1.0}c")) {
            Assert.assertEquals(BeanValidator.class.getAnnotation(Named.class).value(), model.getBean());
            Assert.assertNull(model.getClazz());
        }
    }
}
