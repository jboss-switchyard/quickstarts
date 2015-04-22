/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
