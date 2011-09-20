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

package org.switchyard.test.mixins;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.NamingException;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.Assert;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.test.MockInitialContextFactory;
import org.switchyard.test.SimpleTestDeployment;

import java.util.Set;

/**
 * CDI Test Mix-In for deploying the Weld CDI Standalone Edition container.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIMixIn extends AbstractTestMixIn {

    private Weld _weld;
    private WeldContainer _weldContainer;
    private AbstractDeployment _simpleCdiDeployment;

    @Override
    public void initialize() {
        // Deploy the weld container...
        _weld = new Weld();
        _weldContainer = _weld.initialize();
        _weldContainer.event().select(ContainerInitialized.class).fire(new ContainerInitialized());

        // And bind the BeanManager instance into java:comp...
        try {
            MockInitialContextFactory.getJavaComp().bind("BeanManager", getBeanManager());
        } catch (NamingException e) {
            Assert.fail("Failed to bind BeanManager into 'java:comp'.");
        }
    }

    @Override
    public void before(AbstractDeployment deployment) {
        if (deployment instanceof SimpleTestDeployment) {
            // Not a user defined configuration based test... deploy the Services and Transformers
            // found by the CDI discovery process...
            @SuppressWarnings("unchecked")
            Class<? extends AbstractDeployment> simpleCdiDeploymentType = (Class<? extends AbstractDeployment>) Classes.forName("org.switchyard.component.bean.internal.SimpleCDIDeployment", getClass());
            if (simpleCdiDeploymentType == null) {
                Assert.fail("Failed to locate the SimpleCDIDeployment class on the classpath.  Module must include the SwitchYard Bean Component as one of its depedencies.");
            }
            try {
                _simpleCdiDeployment = simpleCdiDeploymentType.newInstance();
                _simpleCdiDeployment.setParentDeployment(deployment);
                ServiceDomain domain = ServiceDomainManager.createDomain();
                _simpleCdiDeployment.init(domain, ActivatorLoader.createActivators(domain));
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
        if (_weld != null) {
            _weld.shutdown();
            _weld = null;
        } else {
            Thread.dumpStack();
        }
    }

    private Object createBeanInstance(Bean<?> bean) {
        BeanManager beanManager = getBeanManager();
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(null);

        return beanManager.getReference(bean, bean.getBeanClass(), creationalContext);
    }
}
