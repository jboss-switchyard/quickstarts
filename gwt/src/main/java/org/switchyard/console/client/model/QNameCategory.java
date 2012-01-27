/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client.model;

import org.switchyard.console.client.NameTokens;

import com.google.gwt.autobean.shared.AutoBean;

/**
 * QNameCategory
 * 
 * Provides implementations for the non-property methods in HasQName.
 * 
 * @author Rob Cernich
 */
public final class QNameCategory {

    private QNameCategory() {
    }

    /**
     * @param instance the bean instance.
     * @return the "local" part of the bean's name.
     */
    public static String localName(AutoBean<? extends HasQName> instance) {
        String name = instance.as().getName();
        if (name == null) {
            return null;
        }
        return NameTokens.parseQName(name)[1];
    }

    /**
     * @param instance the bean instance.
     * @return the "namespace" part of the bean's name.
     */
    public static String namespace(AutoBean<? extends HasQName> instance) {
        String name = instance.as().getName();
        if (name == null) {
            return null;
        }
        return NameTokens.parseQName(name)[0];
    }
}
