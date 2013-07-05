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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.switchyard.common.cdi.CDIUtil;
import org.switchyard.common.type.classpath.AbstractTypeFilter;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.config.model.Scannable;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.transform.v1.V1TransformsModel;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;
import org.switchyard.transform.internal.TransformerTypes;
import org.switchyard.transform.internal.TransformerUtil;

/**
 * Scanner for {@link org.switchyard.transform.Transformer} implementations.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformSwitchYardScanner implements Scanner<SwitchYardModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        TransformsModel transformsModel = null;

        List<Class<?>> transformerClasses = scanForTransformers(input.getURLs());
        for (Class<?> transformer : transformerClasses) {
            List<TransformerTypes> supportedTransforms = TransformerUtil.listTransformations(transformer);

            for (TransformerTypes supportedTransform : supportedTransforms) {
                JavaTransformModel transformModel = new V1JavaTransformModel();

                String bean = CDIUtil.getNamedAnnotationValue(transformer);
                if (bean != null) {
                    transformModel.setBean(bean);
                } else {
                    transformModel.setClazz(transformer.getName());
                }
                transformModel.setFrom(supportedTransform.getFrom());
                transformModel.setTo(supportedTransform.getTo());

                if (transformsModel == null) {
                    transformsModel = new V1TransformsModel();
                    switchyardModel.setTransforms(transformsModel);
                }
                transformsModel.addTransform(transformModel);
            }
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private List<Class<?>> scanForTransformers(List<URL> urls) throws IOException {
        AbstractTypeFilter filter = new TransformerInstanceOfFilter();
        ClasspathScanner scanner = new ClasspathScanner(filter);

        for (URL url : urls) {
            scanner.scan(url);
        }

        return filter.getMatchedTypes();
    }

    private class TransformerInstanceOfFilter extends AbstractTypeFilter {
        @Override
        public boolean matches(Class<?> clazz) {
            Scannable scannable = clazz.getAnnotation(Scannable.class);
            if (scannable != null && !scannable.value()) {
                // Marked as being non-scannable...
                return false;
            }
            return TransformerUtil.isTransformer(clazz);
        }
    }
}
