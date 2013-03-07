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
 * Maps context properties from and to a source or target object.
 * 
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public interface ContextMapper<D extends BindingData> {

    /**
     * Gets the model.
     * @return the model
     */
    public ContextMapperModel getModel();

    /**
     * Sets the model.
     * @param model the model
     */
    public void setModel(ContextMapperModel model);

    /**
     * Maps a source object's properties to the context.
     * @param source the object to map from
     * @param context the context to map to
     * @throws Exception if there was a problem
     */
    public void mapFrom(D source, Context context) throws Exception;

    /**
     * Maps a context's properties into a target object.
     * @param context the context to map from
     * @param target the target to map to
     * @throws Exception if there was a problem
     */
    public void mapTo(Context context, D target) throws Exception;

}
