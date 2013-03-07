/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.composer;

import org.switchyard.Context;
import org.switchyard.config.model.composer.ContextMapperModel;

/**
 * Base class for ContextMapper, no-op'ing the required methods in case the extender only needs to override one of them.
 *
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public class BaseContextMapper<D extends BindingData> implements ContextMapper<D> {

    private ContextMapperModel _model;

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapperModel getModel() {
        return _model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModel(ContextMapperModel model) {
        _model = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(D source, Context context) throws Exception {
        // No-op; override if desired.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, D target) throws Exception {
        // No-op; override if desired.
    }

}
