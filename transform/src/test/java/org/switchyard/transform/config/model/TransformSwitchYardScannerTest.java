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
