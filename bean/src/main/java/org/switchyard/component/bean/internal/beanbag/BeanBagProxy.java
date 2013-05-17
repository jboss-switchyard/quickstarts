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

package org.switchyard.component.bean.internal.beanbag;

import javax.enterprise.context.ApplicationScoped;

import org.switchyard.Context;
import org.switchyard.Message;
import org.switchyard.component.bean.BeanBag;

/**
 * SwitchYard BeanBag proxy.
 */
@ApplicationScoped
public class BeanBagProxy implements BeanBag {

    private static final ThreadLocal<BeanBag> BEANBAG = new ThreadLocal<BeanBag>();

    @Override
    public Context getInContext() {
        return getBeanBag().getInContext();
    }

    @Override
    public Context getInContext(String reference) {
        return getBeanBag().getInContext(reference);
    }

    @Override
    public Message getOutMessage() {
        return getBeanBag().getOutMessage();
    }

    
    /**
     * Gets the {@link Context} for the current thread.
     * @return the context
     */
    private static BeanBag getBeanBag() {
        BeanBag context = BEANBAG.get();
        if (context == null) {
            throw new IllegalStateException("Illegal call to get the SwitchYard Context; must be called within the execution of an ExchangeHandler chain.");
        }
        return context;
    }

    /**
     * Sets the {@link Context} for the current thread.
     * @param beanBag the context
     */
    public static void setBeanBag(BeanBag beanBag) {
        if (beanBag != null) {
            BEANBAG.set(beanBag);
        } else {
            BEANBAG.remove();
        }
    }

}
