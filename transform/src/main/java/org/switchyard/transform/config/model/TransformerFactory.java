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

package org.switchyard.transform.config.model;

import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.Transformer;

/**
 * Transformer Factory.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformerFactory {

    /**
     * Create a new {@link Transformer} instance from the supplied {@link TransformModel} instance.
     * @param transformModel The TransformModel instance.
     * @return The Transformer instance.
     */
    public static Transformer<?, ?> newTransformer(TransformModel transformModel) {

        // TODO: Need a proper mechanism for building component instances (not just Transformers) from their config Model types Vs hard-wiring at this point in the code !  This makes it impossible for 3rd party impls.

        if(transformModel instanceof JavaTransformModel) {
            String className = ((JavaTransformModel) transformModel).getClazz();
            try {
                return (Transformer) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error constructing Transformer instance for class '" + className + "'.", e);
            }
        }

        throw new RuntimeException("Unknown TransformModel type '" + transformModel.getClass().getName() + "'.");
    }

}
