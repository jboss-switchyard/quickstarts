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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.exception.DuplicateTransformerException;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.transform.config.model.JavaTransformModel;

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
    private List<Transformer<?,?>> _transformers = new LinkedList<Transformer<?,?>>();
    /**
     * The registry instance into which the transforms were loaded.
     */
    private TransformerRegistry _transformerRegistry;
    
    private Map<Class<?>, TransformerFactory<?>> _transformerFactories = 
            new HashMap<Class<?>, TransformerFactory<?>>();

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
     * @throws DuplicateTransformerException an existing transformer has already been registered for the from and to types
     */
    public void registerTransformers(TransformsModel transforms) throws DuplicateTransformerException {
        if (transforms == null) {
            return;
        }

        try {
            for (TransformModel transformModel : transforms.getTransforms()) {
                Collection<Transformer<?, ?>> transformers = newTransformers(transformModel);
                transformerLoop : for (Transformer<?, ?> transformer : transformers) {
                    if (_transformerRegistry.hasTransformer(transformer.getFrom(), transformer.getTo())) {
                        Transformer<?, ?> registeredTransformer = _transformerRegistry.getTransformer(transformer.getFrom(), transformer.getTo());
                        boolean test = false;
                        testLoop : for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                            if (ste.getClassName().startsWith("org.switchyard.test.")) {
                                test = true;
                                break testLoop;
                            }
                        }
                        String msg = "Failed to register Transformer '" + toDescription(transformer)
                                + "'.  A Transformer for these types is already registered: '"
                                + toDescription(registeredTransformer) + "'.";
                        if (test) {
                            _log.trace(msg);
                            continue transformerLoop;
                        } else {
                            throw new DuplicateTransformerException(msg);
                        }
                    }
                    _log.debug("Adding transformer =>"
                            + " From: " + transformer.getFrom()
                            + ", To:" + transformer.getTo());
                    _transformerRegistry.addTransformer(transformer);
                    _transformers.add(transformer);
                }
            }
        } catch (DuplicateTransformerException e) {
            throw e;
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
        for (Transformer<?, ?> transformer : _transformers) {
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
                } catch (final DuplicateTransformerException e) {
                    _log.debug(e.getMessage());
                } finally {
                    configStream.close();
                }
            }
        } catch (IOException e) {
            throw new SwitchYardException("Error reading out-of-the-box Transformer configurations from classpath (" + TRANSFORMS_XML + ").", e);
        }
    }

    /**
     * Create a new {@link org.switchyard.transform.Transformer} instance from the supplied {@link TransformModel} instance.
     * @param transformModel The TransformModel instance.
     * @return The Transformer instance.
     */
    public Transformer<?, ?> newTransformer(TransformModel transformModel) {
        return newTransformers(transformModel).iterator().next();
    }

    /**
     * Create a Collection of {@link Transformer} instances from the supplied {@link TransformModel} instance.
     * @param transformModel The TransformModel instance.
     * @return The Transformer instance.
     */
    public Collection<Transformer<?, ?>> newTransformers(TransformModel transformModel) {

        Collection<Transformer<?, ?>> transformers = null;

        if (transformModel instanceof JavaTransformModel) {
            String className = ((JavaTransformModel) transformModel).getClazz();
                Class<?> transformClass = Classes.forName(className, TransformerUtil.class);
                if (transformClass == null) {
                    throw new SwitchYardException("Unable to load transformer class " + className);
                }
                transformers = TransformerUtil.newTransformers(transformClass, transformModel.getFrom(), transformModel.getTo());
            
        } else {
            TransformerFactory factory = getTransformerFactory(transformModel);

            transformers = new ArrayList<Transformer<?, ?>>();
            transformers.add(factory.newTransformer(transformModel));
        }

        if (transformers == null || transformers.isEmpty()) {
            throw new SwitchYardException("Unknown TransformModel type '" + transformModel.getClass().getName() + "'.");
        }

        return transformers;
    }

    private String toDescription(Transformer<?, ?> transformer) {
        return transformer.getClass().getName() + "(" + transformer.getFrom() + ", " + transformer.getTo() + ")";
    }

    private TransformerFactory<?> getTransformerFactory(TransformModel transformModel) {
        TransformerFactoryClass transformerFactoryClass = transformModel.getClass().getAnnotation(TransformerFactoryClass.class);

        if (transformerFactoryClass == null) {
            throw new SwitchYardException("TransformModel type '" + transformModel.getClass().getName() + "' is not annotated with an @TransformerFactoryClass annotation.");
        }

        Class<?> factoryClass = transformerFactoryClass.value();
        if (!TransformerFactory.class.isAssignableFrom(factoryClass)) {
            throw new SwitchYardException("Invalid TransformerFactory implementation.  Must implement '" + org.switchyard.transform.TransformerFactory.class.getName() + "'.");
        }

        try {
            if (!_transformerFactories.containsKey(factoryClass)) {
                TransformerFactory<?> factory = (TransformerFactory<?>) factoryClass.newInstance();
                _transformerFactories.put(factoryClass, factory);
            }
            
            return _transformerFactories.get(factoryClass);
        } catch (Exception e) {
            throw new SwitchYardException("Failed to create an instance of TransformerFactory '" + factoryClass.getName() + "'.  Class must have a public default constructor and not be abstract.");
        }
    }
}
