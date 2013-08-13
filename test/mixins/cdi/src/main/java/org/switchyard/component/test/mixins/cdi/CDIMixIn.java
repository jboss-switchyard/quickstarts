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

package org.switchyard.component.test.mixins.cdi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.junit.Assert;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.Classes;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.test.MixInDependencies;
import org.switchyard.test.SimpleTestDeployment;
import org.switchyard.test.TestMixIn;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * CDI Test Mix-In for deploying the Weld CDI Standalone Edition container.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@MixInDependencies(required={NamingMixIn.class})
public class CDIMixIn extends AbstractTestMixIn {
    private static final String BINDING_CONTEXT = "java:comp";
    private static final String BEAN_MANAGER_NAME = "BeanManager";
    
    private static final Pattern[] RESOURCE_FILTER_PATTERNS = new Pattern[] {
        Pattern.compile(".*jbpm-.*-services.*beans\\.xml"),
        Pattern.compile(".*jbpm-runtime-manager.*beans\\.xml"),
        Pattern.compile(".*jbpm-human-task-.*beans\\.xml")
    };
    
    private Weld _weld;
    private WeldContainer _weldContainer;
    private AbstractDeployment _simpleCdiDeployment;
    private InjectionTarget<Object> _testInjectionTarget;
    private CreationalContext<Object> _testCreationalContext;

    @Override
    public void initialize() {
        super.initialize();

        // Deploy the weld container...
        _weld = new Weld() {
            @Override
            protected Deployment createDeployment(final ResourceLoader resourceLoader, Bootstrap bootstrap) {
                ResourceLoader filterLoader = new ResourceLoader() {
                    public void cleanup() {
                        resourceLoader.cleanup();
                    }
                    public Class<?> classForName(String name) {
                        return resourceLoader.classForName(name);
                    }
                    public URL getResource(String name) {
                        return filter(resourceLoader.getResource(name));
                    }
                    public Collection<URL> getResources(String name) {
                        Collection<URL> urls = new ArrayList<URL>();
                        for (URL url : resourceLoader.getResources(name)) {
                            url = filter(url);
                            if (url != null) {
                                urls.add(url);
                            }
                        }
                        return urls;
                    }
                    private URL filter(URL url) {
                        if (url != null) {
                            for (Pattern pattern : RESOURCE_FILTER_PATTERNS) {
                                if (pattern.matcher(url.toString()).matches()) {
                                    return null;
                                }
                            }
                        }
                        return url;
                    }
                };
                Deployment deployment = super.createDeployment(filterLoader, bootstrap);
                if (getTestKit() != null) {
                    Set<TestMixIn> optionalDependencies = getTestKit().getOptionalDependencies(CDIMixIn.this);
                    for (TestMixIn dependency : optionalDependencies) {
                        if (dependency instanceof CDIMixInParticipant) {
                           try {
                               ((CDIMixInParticipant) dependency).participate(deployment);
                            } catch (Exception e) {
                                throw new SwitchYardException("Can not initialize Weld due CDIMixIn initialization error", e);
                            }
                        }
                    }
                }
                return deployment;
            }
        };
        _weldContainer = _weld.initialize();
        _weldContainer.event().select(ContainerInitialized.class).fire(new ContainerInitialized());

        // And bind the BeanManager instance into java:comp...
        try {
            Context ctx = (Context) new InitialContext().lookup(BINDING_CONTEXT);
            ctx.rebind(BEAN_MANAGER_NAME, getBeanManager());
        } catch (NamingException e) {
            e.printStackTrace();
            Assert.fail("Failed to bind BeanManager into '" + BINDING_CONTEXT + "'.");
        }

        injectCDIBeans();
    }

    @Override
    public void before(AbstractDeployment deployment) {
        if (deployment instanceof SimpleTestDeployment) {
            // Not a user defined configuration based test... deploy the Services and Transformers
            // found by the CDI discovery process...
            @SuppressWarnings("unchecked")
            Class<? extends AbstractDeployment> simpleCdiDeploymentType = (Class<? extends AbstractDeployment>) Classes.forName("org.switchyard.component.bean.internal.SimpleCDIDeployment", getClass());
            if (simpleCdiDeploymentType == null) {
                if (getTestKit() != null && getTestKit().getTestInstance().getClass().getPackage() == CDIMixIn.class.getPackage()) {
                    return;  // not fatal for unit tests of CDIMixIn since they can't depend on the bean component as this would create a cyclic Maven dependency.
                } else {
                    Assert.fail("Failed to locate the SimpleCDIDeployment class on the classpath.  Module must include the SwitchYard Bean Component as one of its depedencies.");
                }
            }
            try {
                _simpleCdiDeployment = simpleCdiDeploymentType.newInstance();
                _simpleCdiDeployment.setParentDeployment(deployment);
                ServiceDomain domain = new ServiceDomainManager().createDomain();
                _simpleCdiDeployment.init(domain, getTestKit().getActivators());
                _simpleCdiDeployment.start();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Failed to manually deploy Bean Service.  Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Get the CDI {@link BeanManager}.
     * @return The CDI {@link BeanManager}.
     */
    public BeanManager getBeanManager() {
        return _weldContainer.getBeanManager();
    }

    /**
     * Get the {@link javax.inject.Named @Named} CDI {@link Bean}.
     * @param name The name of the bean.
     * @return The {@link javax.inject.Named @Named} CDI {@link Bean}.
     */
    public Bean<?> getBean(String name) {
        BeanManager beanManager = getBeanManager();
        Set<Bean<?>> beans = beanManager.getBeans(name);

        if (beans != null && !beans.isEmpty()) {
            return beans.iterator().next();
        } else {
            throw new IllegalStateException("Unable to find CDI bean @Named '" + name + "'.  \n\t - Check for a typo in the name. \n\t - Make sure the Class is annotated with the @Name annotation.");
        }
    }

    /**
     * Get the CDI {@link Bean} associated with CDI the specified type.
     * @param type The bean type.
     * @return The the CDI {@link Bean} associated with CDI the specified type.
     */
    public Bean getBean(Class<?> type) {
        BeanManager beanManager = getBeanManager();
        Set<Bean<?>> beans = beanManager.getBeans(type);

        if (beans != null && !beans.isEmpty()) {
            return beans.iterator().next();
        } else {
            throw new IllegalStateException("Unable to find CDI bean of type '" + type.getName() + "'.");
        }
    }

    /**
     * Get the object instance associated with a {@link javax.inject.Named @Named} CDI {@link Bean}.
     * @param name The name of the bean.
     * @return The object instance associated with a {@link javax.inject.Named @Named} CDI {@link Bean}.
     */
    public Object getObject(String name) {
        return createBeanInstance(getBean(name));
    }

    /**
     * Get the object instance associated with a {@link javax.inject.Named @Named} CDI {@link Bean}.
     * @param name The name of the bean.
     * @param type The bean type.
     * @return The object instance associated with a {@link javax.inject.Named @Named} CDI {@link Bean}.
     *
     * @param <T> Type.
     */
    public <T> T getObject(String name, Class<T> type) {
        return type.cast(getObject(name));
    }

    /**
     * Get the object instance associated with CDI {@link Bean} of the specified type.
     * @param type The bean type.
     * @return The object instance associated with CDI {@link Bean} of the specified type.
     *
     * @param <T> Type.
     */
    public <T> T getObject(Class<T> type) {
        return type.cast(createBeanInstance(getBean(type)));
    }

    @Override
    public void after(AbstractDeployment deployment) {
        if (_simpleCdiDeployment != null) {
            _simpleCdiDeployment.stop();
            _simpleCdiDeployment.destroy();
        }
    }

    @Override
    public synchronized void uninitialize() {
        disposeCDIBeans();

        if (_weld != null) {
            _weld.shutdown();
            _weld = null;
        } else {
            Thread.dumpStack();
        }

        // Clean up JNDI tree
        super.uninitialize();
    }

    private Object createBeanInstance(Bean<?> bean) {
        BeanManager beanManager = getBeanManager();
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(null);

        return beanManager.getReference(bean, bean.getBeanClass(), creationalContext);
    }

    @SuppressWarnings("unchecked")
    private void injectCDIBeans() {
        BeanManager beanManager = getBeanManager();
        Object testInstance = getTestKit() != null ? getTestKit().getTestInstance() : null;
        if (beanManager != null && testInstance != null) {
            // delegate dependency injection and lifecycle callbacks to the CDI container
            AnnotatedType<Object> type = beanManager.createAnnotatedType((Class<Object>) testInstance.getClass());
            _testInjectionTarget = beanManager.createInjectionTarget(type);
            _testCreationalContext = beanManager.createCreationalContext(null);
            _testInjectionTarget.inject(getTestKit().getTestInstance(), _testCreationalContext);
            _testInjectionTarget.postConstruct(getTestKit().getTestInstance());
        }
    }

    private void disposeCDIBeans() {
        if (_testInjectionTarget != null) {
            Object testInstance = getTestKit().getTestInstance();
            _testInjectionTarget.preDestroy(testInstance);
            _testInjectionTarget.dispose(testInstance);
            _testCreationalContext.release();
        }
    }
}
