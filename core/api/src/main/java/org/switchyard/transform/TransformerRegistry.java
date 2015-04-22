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

package org.switchyard.transform;

import java.util.List;
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
    
    /**
     * Get a list of currently registered Transformers.
     * @return ArrayList<Transformer<?,?>> list of currently registered transformers.
     */
    List<Transformer<?,?>> getRegisteredTransformers();
    
    /**
     * Returns a list of transformers that transform from the specified type.
     * @param type the type transformed from
     * @return list of transformers
     */
    public List<Transformer<?,?>> getTransformersFrom(QName type);
    
    /**
     * Returns a list of transformers that transform to the specified type.
     * @param type the type transformed to
     * @return list of transformers
     */
    public List<Transformer<?,?>> getTransformersTo(QName type);
    
    /**
     * Get a transform sequence wiring transformers.
     * @param from from
     * @param to to
     * @return transformer
     */
    TransformSequence getTransformSequence(QName from, QName to);
    
    /**
     * Set an instance of a TransformResolver.
     * @param resolver resolver
     * @return 
     */    
    void setTransfomResolver(TransformResolver resolver);

}
