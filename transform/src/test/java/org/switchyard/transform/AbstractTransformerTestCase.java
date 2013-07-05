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

package org.switchyard.transform;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.transform.internal.TransformerRegistryLoader;

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
            switchyardConfig = new ModelPuller<SwitchYardModel>().pull(swConfigStream);
        } finally {
            swConfigStream.close();
        }

        TransformsModel transforms = switchyardConfig.getTransforms();

        TransformModel transformModel = transforms.getTransforms().get(0);

        if (transformModel == null) {
            Assert.fail("No smooks config.");
        }
        TransformerRegistryLoader trl = new TransformerRegistryLoader(new BaseTransformerRegistry());
        return trl.newTransformer(transformModel);
    }
}
