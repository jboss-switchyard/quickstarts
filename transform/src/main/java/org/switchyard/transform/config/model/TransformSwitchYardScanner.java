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
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;

import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.transform.v1.V1TransformsModel;
import org.switchyard.config.util.classpath.ClasspathScanner;
import org.switchyard.config.util.classpath.InstanceOfFilter;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;

/**
 * Scanner for {@link Transformer} implementations.
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
            if (transformer.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(transformer.getModifiers())) {
                continue;
            }

            JavaTransformModel transformModel = new V1JavaTransformModel();
            // Need to create an instance to get the transform type info...
            try {
                Transformer<?,?> transformerInst = (Transformer<?,?>) transformer.newInstance();
                transformModel.setFrom(transformerInst.getFrom());
                transformModel.setTo(transformerInst.getTo());
            } catch (Exception e) {
                throw new IOException("Error creating instance of Transformer '" + transformer.getName() + "'.  May not contain a public default constructor.", e);
            }
            transformModel.setClazz(transformer.getName());

            if (transformsModel == null) {
                transformsModel = new V1TransformsModel();
                switchyardModel.setTransforms(transformsModel);
            }
            transformsModel.addTransform(transformModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private List<Class<?>> scanForTransformers(List<URL> urls) throws IOException {
        InstanceOfFilter filter = new InstanceOfFilter(Transformer.class);
        ClasspathScanner scanner = new ClasspathScanner(filter);

        for (URL url : urls) {
            scanner.scan(url);
        }

        return filter.getMatchedTypes();
    }
}
