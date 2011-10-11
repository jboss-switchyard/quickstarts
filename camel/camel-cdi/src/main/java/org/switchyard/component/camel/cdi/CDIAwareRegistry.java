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

package org.switchyard.component.camel.cdi;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.Registry;
import org.apache.log4j.Logger;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.CDIBean;
import org.switchyard.exception.SwitchYardException;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CDI Aware Camel {@link Registry}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIAwareRegistry implements Registry {

    /**
     * Logger
     */
    private static Logger _logger = Logger.getLogger(CDIAwareRegistry.class);

    private BeanDeploymentMetaData _beanDeploymentMetaData;
    private Registry _baseRegistry;

    /**
     * Public constructor.
     */
    public CDIAwareRegistry() {
        try {
            this._beanDeploymentMetaData = BeanDeploymentMetaData.lookupBeanDeploymentMetaData();
        } catch (SwitchYardException e) {
            _logger.debug("CDIAwareRegistry falling back to default Jndi only Registry behavior.  Deployment does not contain bean metadata.");
        }
        this._baseRegistry = new JndiRegistry();
    }

    @Override
    public Object lookup(final String name) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Looking up bean using name = [" + name + "] in CDI registry ...");
        }

        Bean bean = getBean(name);
        if (bean == null) {
            return _baseRegistry.lookup(name);
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("Found SwitchYard Service bean matching name = [" + name + "] in CDI registry.");
        }

        return createBeanInstance(bean);
    }

    @Override
    public <T> T lookup(final String name, final Class<T> type) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Looking up bean using name = [" + name + "] having expected type = [" + type.getName() + "] in CDI registry ...");
        }

        return type.cast(lookup(name));
    }

    @Override
    public <T> Map<String, T> lookupByType(final Class<T> type) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Looking up all beans having expected type = [" + type.getName() + "] in CDI registry ...");
        }

        List<CDIBean> serviceBeans = getBeans(type);
        if (serviceBeans.isEmpty()) {
            return _baseRegistry.lookupByType(type);
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("Found [" + Integer.valueOf(serviceBeans.size()) + "] beans having expected type = [" + type.getName() + "] in CDI registry.");
        }

        Map<String, T> beansByName = new HashMap<String, T>(serviceBeans.size());
        for (CDIBean cdiBean : serviceBeans) {
            String beanName = cdiBean.getBean().getName();

            // The bean must be annotated with @Named... we don't assume any default name.
            // We can always change that down the line if we think there's a hard need for it.
            // See http://relation.to/Bloggers/WhyDontCDIBeansHaveNamesByDefault
            if (beanName != null) {
                beansByName.put(beanName, type.cast(createBeanInstance(cdiBean.getBean())));
            }
        }

        return beansByName;
    }

    private Bean<?> getBean(String name) {
        if (_beanDeploymentMetaData != null) {
            // The bean must be annotated with @Named... we don't assume any default name.
            // We can always change that down the line if we think there's a hard need for it.
            // See http://relation.to/Bloggers/WhyDontCDIBeansHaveNamesByDefault
            Set<Bean<?>> beans = _beanDeploymentMetaData.getBeanManager().getBeans(name);
            if (beans != null && !beans.isEmpty()) {
                return beans.iterator().next();
            }
        }

        return null;
    }

    private List<CDIBean> getBeans(Class<?> type) {
        List<CDIBean> cdiBeans = new ArrayList<CDIBean>();

        if (_beanDeploymentMetaData != null) {
            for (CDIBean cdiBean : _beanDeploymentMetaData.getDeploymentBeans()) {
                if (type.isAssignableFrom(cdiBean.getBean().getBeanClass())) {
                    cdiBeans.add(cdiBean);
                }
            }
        }

        return cdiBeans;
    }

    private Object createBeanInstance(Bean bean) {
        BeanManager beanManager = _beanDeploymentMetaData.getBeanManager();
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(null);

        return beanManager.getReference(bean, bean.getBeanClass(), creationalContext);
    }
}
