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

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.config.model.transformers.ATransformer;
import org.switchyard.transform.config.model.transformers.BTransformer;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformerSwitchYardScannerTest {

    @Test
    public void test() throws IOException {
        TransformerSwitchYardScanner scanner = new TransformerSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the transform module !!
        urls.add(new File("./target/test-classes").toURI().toURL());

        List<TransformModel> models = scanner.scan(urls);

        Assert.assertEquals(2, models.size());
        assertModelInstanceOK((V1JavaTransformModel) models.get(0));
        assertModelInstanceOK((V1JavaTransformModel) models.get(1));
    }

    private void assertModelInstanceOK(V1JavaTransformModel model) {
        if (model.getFrom().toString().equals("{http://www.switchyard.org}a")) {
            Assert.assertEquals(ATransformer.class.getName(), model.getClazz());
            Assert.assertEquals("{http://www.switchyard.org}b", model.getTo().toString());
        } else if (model.getFrom().toString().equals("{http://www.switchyard.org}b")) {
            Assert.assertEquals(BTransformer.class.getName(), model.getClazz());
            Assert.assertEquals("{http://www.switchyard.org}c", model.getTo().toString());
        }
    }
}
