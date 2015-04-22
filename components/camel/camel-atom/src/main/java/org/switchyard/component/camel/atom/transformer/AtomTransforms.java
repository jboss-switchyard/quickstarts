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
package org.switchyard.component.camel.atom.transformer;

import org.apache.abdera.parser.stax.FOMEntry;

import org.switchyard.annotations.Transformer;

/**
 * Provide a default transformer from org.apache.abdera.parser.stax.FOMEntry->String,
 * which is useful for creating a simple service using the Camel Atom binding.
 * 
 * @author tcunning
 */
public class AtomTransforms {
    
    /**
     * Singleton instance.
     */
    public static final AtomTransforms TRANSFORMER = new AtomTransforms();
    
    /**
     * Transform FOMEntry->String.
     * @param entry entry
     * @return String
     */
    @Transformer
    public String toString(FOMEntry entry) {
        return entry.toString();
    }

}
