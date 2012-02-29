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

package org.switchyard.event;

import java.util.EventObject;

import org.switchyard.transform.Transformer;

/**
 * Fired when a transformer is removed from the domain.
 */
public class TransformerRemovedEvent extends EventObject {

    private static final long serialVersionUID = -4987595062503824361L;
    
    /**
     * Creates a new TransformerRemovedEvent.
     * @param transformer the transformer that was removed
     */
    public TransformerRemovedEvent(Transformer<?,?> transformer) {
        super(transformer);
    }
    
    /**
     * Get the removed transformer.
     * @return removed transformer
     */
    public Transformer<?,?> getTransformer() {
        return (Transformer<?,?>)getSource();
    }
}
