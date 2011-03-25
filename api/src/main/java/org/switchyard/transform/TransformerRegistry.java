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

package org.switchyard.transform;

import javax.xml.namespace.QName;

/**
 * Registry for transformers.
 */
public interface TransformerRegistry {

    /**
     * Add a transformer.
     * @param transformer transformer
     * @return {@code this} TransformRegistry instance.
     */
    TransformerRegistry addTransformer(Transformer<?, ?> transformer);

    /**
     * Add a transformer.
     * @param transformer transformer
     * @param from from
     * @param to to
     * @return {@code this} TransformRegistry instance.
     */
    TransformerRegistry addTransformer(Transformer<?, ?> transformer, QName from, QName to);

    /**
     * Remove a transformer.
     * @param transformer transformer
     * @return status of removal
     */
    boolean removeTransformer(Transformer<?, ?> transformer);

    /**
     * Does the registry have a transformer for the specified types.
     * @param from from
     * @param to to
     * @return True if it has a transformer, otherwise false.
     */
    boolean hasTransformer(QName from, QName to);

    /**
     * Get a transformer.
     * @param from from
     * @param to to
     * @return transformer
     */
    Transformer<?, ?> getTransformer(QName from, QName to);
}
