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
