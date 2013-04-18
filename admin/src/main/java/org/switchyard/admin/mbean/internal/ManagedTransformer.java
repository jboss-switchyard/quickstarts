/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.admin.mbean.internal;

import org.switchyard.admin.Transformer;
import org.switchyard.admin.mbean.TransformerMXBean;

/**
 * Implementation of TransformerMXBean.
 */
public class ManagedTransformer implements TransformerMXBean {
    
    private Transformer _transformer;
    
    /**
     * Create a new ManagedTransformer instance.
     * @param transformer delegate reference to admin Transformer.
     */
    public ManagedTransformer(Transformer transformer) {
        _transformer = transformer;
    }

    @Override
    public String getFrom() {
        return _transformer.getFrom().toString();
    }

    @Override
    public String getTo() {
        return _transformer.getTo().toString();
    }

    @Override
    public String getType() {
        return _transformer.getType();
    }

}
