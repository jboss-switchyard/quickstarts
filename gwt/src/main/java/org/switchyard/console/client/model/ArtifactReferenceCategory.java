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

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * QNameCategory
 * 
 * Provides implementations for the non-property methods in HasQName.
 * 
 * @author Rob Cernich
 */
public final class ArtifactReferenceCategory {

    private ArtifactReferenceCategory() {
    }

    /**
     * @param instance the bean instance.
     * @return a unique identifier for the instance.
     */
    public static String key(AutoBean<? extends ArtifactReference> instance) {
        ArtifactReference ar = instance.as();
        return "" + ar.getName() + "::" + ar.getUrl();
    }

}
