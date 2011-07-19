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

package org.switchyard.component.bean.deploy;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * CDI Bean instance.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIBean {

    private Bean _bean;
    private BeanManager _beanManager;

    /**
     * Public constructor.
     * @param bean The bean isntance.
     * @param beanManager The bean manager.
     */
    public CDIBean(Bean bean, BeanManager beanManager) {
        this._bean = bean;
        this._beanManager = beanManager;
    }

    /**
     * Get the CDI Bean instance.
     * @return The CDI bean instance.
     */
    public Bean getBean() {
        return _bean;
    }

    /**
     * Get the CDI BeanManager instance.
     * @return The CDI BeanManager instance.
     */
    public BeanManager getBeanManager() {
        return _beanManager;
    }
}
