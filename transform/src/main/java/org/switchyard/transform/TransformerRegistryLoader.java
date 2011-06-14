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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.exception.SwitchYardException;

/**
 * {@link TransformerRegistry} loader class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformerRegistryLoader {

    /**
     * Logger.
     */
    private static Logger _log = Logger.getLogger(TransformerRegistryLoader.class);
    /**
     * Classpath location for out-of-the-box transformation configurations.
     */
    public static final String TRANSFORMS_XML = "META-INF/switchyard/transforms.xml";

    /**
     * Transformers.
     */
    private List<Transformer> _transformers = new LinkedList<Transformer>();
    /**
     * The registry instance into which the transforms were loaded.
     */
    private TransformerRegistry _transformerRegistry;

    /**
     * Public constructor.
     * @param transformerRegistry The registry instance.
     */
    public TransformerRegistryLoader(TransformerRegistry transformerRegistry) {
        if (transformerRegistry == null) {
            throw new IllegalArgumentException("null 'transformerRegistry' argument.");
        }
        this._transformerRegistry = transformerRegistry;
    }

    /**
     * Register a set of transformers in the transform registry associated with this deployment.
     * @param transforms The transforms model.
     */
    public void registerTransformers(TransformsModel transforms) {
        if (transforms == null) {
            return;
        }

        try {
            for (TransformModel transformModel : transforms.getTransforms()) {
                Collection<Transformer<?, ?>> transformers = TransformerUtil.newTransformers(transformModel);

                for (Transformer<?, ?> transformer : transformers) {
                    if (_transformerRegistry.hasTransformer(transformer.getFrom(), transformer.getTo())) {
                        Transformer<?, ?> registeredTransformer = _transformerRegistry.getTransformer(transformer.getFrom(), transformer.getTo());
                        throw new SwitchYardException("Failed to register Transformer '" + toDescription(transformer)
                                + "'.  A Transformer for these types is already registered: '"
                                + toDescription(registeredTransformer) + "'.");
                    }

                    _log.debug("Adding transformer =>"
                            + " From: " + transformer.getFrom()
                            + ", To:" + transformer.getTo());
                    _transformerRegistry.addTransformer(transformer);
                    _transformers.add(transformer);
                }
            }
        } catch (RuntimeException e) {
            // If there was an exception for any reason... remove all Transformer instance that have
            // already been registered with the domain...
            unregisterTransformers();
            throw e;
        }
    }

    /**
     * Unregister all transformers.
     */
    public void unregisterTransformers() {
        for (Transformer transformer : _transformers) {
            _transformerRegistry.removeTransformer(transformer);
        }
    }

    /**
     * Load the out of the box transformers.
     * <p/>
     * Scans the classpath for {@link #TRANSFORMS_XML} runtime configuration resources.
     */
    public void loadOOTBTransforms() {
        try {
            List<URL> resources = Classes.getResources(TRANSFORMS_XML, getClass());

            for (URL resource : resources) {
                InputStream configStream = resource.openStream();

                try {
                    TransformsModel transformsModel = new ModelPuller<TransformsModel>().pull(configStream);
                    registerTransformers(transformsModel);
                } finally {
                    configStream.close();
                }
            }
        } catch (IOException e) {
            throw new SwitchYardException("Error reading out-of-the-box Transformer configurations from classpath (" + TRANSFORMS_XML + ").", e);
        }
    }

    private String toDescription(Transformer<?, ?> transformer) {
        return transformer.getClass().getName() + "(" + transformer.getFrom() + ", " + transformer.getTo() + ")";
    }
}
