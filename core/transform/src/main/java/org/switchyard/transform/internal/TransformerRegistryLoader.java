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

package org.switchyard.transform.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.common.cdi.CDIUtil;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
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

    private ServiceDomain _serviceDomain;

    /**
     * Public constructor.
     * @param domain ServiceDomain instance.
     */
    public TransformerRegistryLoader(ServiceDomain domain) {
        if (domain == null) {
            throw TransformMessages.MESSAGES.nullServiceDomainArgument();
        }
        this._serviceDomain = domain;
        if (_serviceDomain.getTransformerRegistry() == null) {
            throw TransformMessages.MESSAGES.nullTransformerRegistryArgument();
        }
       this._transformerRegistry = _serviceDomain.getTransformerRegistry();
    }

    /**
     * Public constructor.
     * @param transformerRegistry The registry instance.
     */
    public TransformerRegistryLoader(TransformerRegistry transformerRegistry) {
        if (transformerRegistry == null) {
            throw TransformMessages.MESSAGES.nullTransformerRegistryArgument();
        }
        this._transformerRegistry = transformerRegistry;
        this._serviceDomain = null;
    }

    /**
     * Register a set of transformers in the transform registry associated with this deployment.
     * @param transforms The transforms model.
     * @throws DuplicateTransformerException an existing transformer has already been registered for the from and to types
     */
    public void registerTransformers(TransformsModel transforms) throws DuplicateTransformerException {
        registerTransformers(transforms, true);
    }

    /**
     * Register a set of transformers in the transform registry associated with this deployment.
     * @param transforms The transforms model.
     * @param failOnError false to eat duplicate exceptions
     * @throws DuplicateTransformerException an existing transformer has already been registered for the from and to types
     */
    public void registerTransformers(TransformsModel transforms, boolean failOnError) throws DuplicateTransformerException {
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
                        } else if (failOnError) {
                            throw new DuplicateTransformerException(msg);
                        } else {
                            _log.debug(msg);
                        }
                        continue transformerLoop;
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
            List<URL> resources = getResources(TRANSFORMS_XML);

            for (URL resource : resources) {
                InputStream configStream = resource.openStream();

                try {
                    TransformsModel transformsModel = new ModelPuller<TransformsModel>().pull(configStream);
                    registerTransformers(transformsModel, false);
                } catch (final DuplicateTransformerException e) {
                    _log.debug(e.getMessage());
                } finally {
                    configStream.close();
                }
            }
        } catch (IOException e) {
            throw TransformMessages.MESSAGES.errorReadingTransformerConfig(TRANSFORMS_XML, e);
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
            JavaTransformModel javaTransformModel = JavaTransformModel.class.cast(transformModel);
            String bean = javaTransformModel.getBean();
            if (bean != null) {
                if (CDIUtil.lookupBeanManager() == null) {
                    throw TransformMessages.MESSAGES.cdiBeanManagerNotFound();
                }
                Object transformer = CDIUtil.lookupBean(bean);
                if (transformer == null) {
                    throw TransformMessages.MESSAGES.beanNotFoundInCDIRegistry(bean);                    
                }
                transformers = TransformerUtil.newTransformers(transformer, transformModel.getFrom(), transformModel.getTo());
            } else {
                String className = ((JavaTransformModel) transformModel).getClazz();
                if (className == null) {
                    throw TransformMessages.MESSAGES.beanOrClassMustBeSpecified();
                }
                Class<?> transformClass = getClass(className);
                if (transformClass == null) {
                    throw TransformMessages.MESSAGES.unableToLoadTransformerClass(className);
                }
                transformers = TransformerUtil.newTransformers(transformClass, transformModel.getFrom(), transformModel.getTo());
            }
        } else {
            TransformerFactory factory = getTransformerFactory(transformModel);

            transformers = new ArrayList<Transformer<?, ?>>();
            transformers.add(factory.newTransformer(_serviceDomain, transformModel));
        }

        if (transformers == null || transformers.isEmpty()) {
            throw TransformMessages.MESSAGES.unknownTransformModel(transformModel.getClass().getName());
        }

        return transformers;
    }
    
    /**
     * Overridable method for resolving a class path resource which allows environments
     * like OSGi to customize resolution.
     */
    protected List<URL> getResources(String path) throws java.io.IOException {
        return Classes.getResources(path, getClass());
    }
    
    /**
     * Overridable method for resolving a class which allows environments like OSGi 
     * to customize resolution.
     */
    protected Class<?> getClass(String className) {
        return Classes.forName(className, TransformerUtil.class);
    }

    private String toDescription(Transformer<?, ?> transformer) {
        return transformer.getClass().getName() + "(" + transformer.getFrom() + ", " + transformer.getTo() + ")";
    }

    private TransformerFactory<?> getTransformerFactory(TransformModel transformModel) {
        TransformerFactoryClass transformerFactoryClass = transformModel.getClass().getAnnotation(TransformerFactoryClass.class);

        if (transformerFactoryClass == null) {
            TransformMessages.MESSAGES.transformModelNotAnnotated(transformModel.getClass().getName());
        }

        Class<?> factoryClass = transformerFactoryClass.value();
        if (!TransformerFactory.class.isAssignableFrom(factoryClass)) {
            TransformMessages.MESSAGES.invalidTransformerFactory(org.switchyard.transform.internal.TransformerFactory.class.getName());
        }

        try {
            if (!_transformerFactories.containsKey(factoryClass)) {
                TransformerFactory<?> factory = (TransformerFactory<?>) factoryClass.newInstance();
                _transformerFactories.put(factoryClass, factory);
            }
            
            return _transformerFactories.get(factoryClass);
        } catch (ClassCastException  e) {
            throw TransformMessages.MESSAGES.failedCreateInstanceofTransformerFactory(factoryClass.getName());
        } catch (IllegalArgumentException iae) {
            throw TransformMessages.MESSAGES.failedCreateInstanceofTransformerFactory(factoryClass.getName());
        } catch (NullPointerException npe) {
            throw TransformMessages.MESSAGES.failedCreateInstanceofTransformerFactory(factoryClass.getName());
        } catch (InstantiationException ie) {
            throw TransformMessages.MESSAGES.failedCreateInstanceofTransformerFactory(factoryClass.getName());
        } catch (IllegalAccessException iae) {
            throw TransformMessages.MESSAGES.failedCreateInstanceofTransformerFactory(factoryClass.getName());            
        }
    }
}
