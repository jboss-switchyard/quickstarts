/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
import org.switchyard.transform.TransformerTypes;
import org.switchyard.transform.TransformerUtil;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;

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
