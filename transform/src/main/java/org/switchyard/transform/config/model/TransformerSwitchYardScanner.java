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

import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.util.classpath.ClasspathScanner;
import org.switchyard.config.util.classpath.InstanceOfFilter;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Scanner for {@link Transformer} implementations.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformerSwitchYardScanner implements Scanner<TransformModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransformModel> scan(List<URL> urls) throws IOException {
        List<Class<?>> transformerClasses = scanForTransformers(urls);
        List<TransformModel> transformerModels = new ArrayList<TransformModel>();

        for (Class transformer : transformerClasses) {
            if (transformer.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(transformer.getModifiers())) {
                continue;
            }

            V1JavaTransformModel transformModel = new V1JavaTransformModel();

            // Need to create an instance to get the transform type info...
            try {
                Transformer transformerInst = (Transformer) transformer.newInstance();

                transformModel.setFrom(transformerInst.getFrom());
                transformModel.setTo(transformerInst.getTo());
            } catch (Exception e) {
                throw new IOException("Error creating instance of Transformer '" + transformer.getName() + "'.  May not contain a public default constructor.", e);
            }

            transformModel.setClazz(transformer.getName());
            transformerModels.add(transformModel);
        }

        return transformerModels;
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
