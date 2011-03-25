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

package org.switchyard.transform.config.model;

import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.util.Classes;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.smooks.internal.SmooksTransformFactory;

/**
 * Transformer Factory.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class TransformerFactory {

    private TransformerFactory() {}

    /**
     * Create a new {@link Transformer} instance from the supplied {@link TransformModel} instance.
     * @param transformModel The TransformModel instance.
     * @return The Transformer instance.
     */
    public static Transformer<?, ?> newTransformer(TransformModel transformModel) {

        // TODO: Need a proper mechanism for building component instances (not just Transformers) from their config Model types Vs hard-wiring at this point in the code !  This makes it impossible for 3rd party impls.

        Transformer<?, ?> transformer = null;

        if (transformModel instanceof JavaTransformModel) {
            String className = ((JavaTransformModel) transformModel).getClazz();
            try {
                transformer = (Transformer<?,?>) Classes.forName(className, TransformerFactory.class).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error constructing Transformer instance for class '" + className + "'.", e);
            }
        } else if (transformModel instanceof SmooksTransformModel) {
            transformer = SmooksTransformFactory.newTransformer((SmooksTransformModel) transformModel);
        }

        if(transformer == null) {
            throw new RuntimeException("Unknown TransformModel type '" + transformModel.getClass().getName() + "'.");
        }

        transformer.setFrom(transformModel.getFrom());
        transformer.setTo(transformModel.getTo());

        return transformer;
    }

}
