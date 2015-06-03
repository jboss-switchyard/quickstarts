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

package org.switchyard.transform.config.model;

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
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.config.model.transformers.ATransformer;
import org.switchyard.transform.config.model.transformers.BTransformer;
import org.switchyard.transform.config.model.transformers.BeanTransformer;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformSwitchYardScannerTest {

    @Test
    public void test() throws IOException {
        TransformSwitchYardScanner scanner = new TransformSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the transform module !!
        urls.add(new File("./target/test-classes").toURI().toURL());

        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
        SwitchYardModel switchyard = scanner.scan(input).getModel();
        List<TransformModel> models = switchyard.getTransforms().getTransforms();

        Assert.assertEquals(11, models.size());
        assertModelInstanceOK((JavaTransformModel) models.get(0));
        assertModelInstanceOK((JavaTransformModel) models.get(1));
        assertModelInstanceOK((JavaTransformModel) models.get(2));
    }

    private void assertModelInstanceOK(JavaTransformModel model) {
        if (model.getFrom().toString().equals("{urn:switchyard-transform:test-transformers:1.0}a")) {
            Assert.assertEquals(ATransformer.class.getName(), model.getClazz());
            Assert.assertEquals("{urn:switchyard-transform:test-transformers:1.0}b", model.getTo().toString());
        } else if (model.getFrom().toString().equals("{urn:switchyard-transform:test-transformers:1.0}b")) {
            Assert.assertEquals(BTransformer.class.getName(), model.getClazz());
            Assert.assertEquals("{urn:switchyard-transform:test-transformers:1.0}c", model.getTo().toString());
        } else if (model.getFrom().toString().equals("{urn:switchyard-transform:test-transformers:1.0}c")) {
            Assert.assertEquals(BeanTransformer.class.getAnnotation(Named.class).value(), model.getBean());
            Assert.assertNull(model.getClazz());
            Assert.assertEquals("{urn:switchyard-transform:test-transformers:1.0}a", model.getTo().toString());
        }
    }
}
