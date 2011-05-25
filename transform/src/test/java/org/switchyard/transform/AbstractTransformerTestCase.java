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

package org.switchyard.transform;

import org.junit.Assert;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.transform.config.model.TransformerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractTransformerTestCase {

    protected Transformer getTransformer(String config) throws IOException {
        InputStream swConfigStream = Classes.getResourceAsStream(config, getClass());

        if (swConfigStream == null) {
            Assert.fail("null config stream.");
        }

        SwitchYardModel switchyardConfig;
        try {
            switchyardConfig = new ModelResource<SwitchYardModel>().pull(swConfigStream);
        } finally {
            swConfigStream.close();
        }

        TransformsModel transforms = switchyardConfig.getTransforms();

        TransformModel transformModel = transforms.getTransforms().get(0);

        if (transformModel == null) {
            Assert.fail("No smooks config.");
        }

        return TransformerFactory.newTransformer(transformModel);
    }
}
