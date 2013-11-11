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
package org.switchyard.transform.xslt.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;

/**
 * Provides a pool of javax.xml.transform.Transformer instances which are
 * created on demand and bounded by a pool maxSize.  If a pooled instance
 * is not available, clients block FIFO style until a transformer is returned
 * to the pool.
 */
public class TransformerPool {
    
    private BlockingQueue<Transformer> _pool;
    private Templates _templates;
    private AtomicInteger _count = new AtomicInteger(0);
    private int _maxSize;
    private ErrorListener _errorListener;
    
    /**
     * Create a new Transformer pool.
     * @param templates template used to create a new Transformer instance
     * @param maxSize maximum size of the pool
     */
    public TransformerPool(Templates templates, int maxSize) {
        this(templates, maxSize, null);
    }
    
    /**
     * Create a new Transformer pool.
     * @param templates template used to create a new Transformer instance
     * @param maxSize maximum size of the pool
     * @param errorListener error listener
     */
    public TransformerPool(Templates templates, int maxSize, ErrorListener errorListener) {
        _templates = templates;
        _maxSize = maxSize;
        _errorListener = errorListener;
        _pool = new ArrayBlockingQueue<Transformer>(maxSize, true);
        
    }

    /**
     * Asks the pool for an available Transformer instance.  If a pooled
     * instance is available, then it is returned immediately.  If not, 
     * a new instance will be created and added to the pool if the pool
     * has not grown to maxSize yet.  Blocked clients are served in 
     * FIFO order.
     * @return Transformer instance
     * @throws Exception failed during creation of a transformer instance
     */
    public Transformer take() throws Exception {
        Transformer t = _pool.poll();
        if (t != null) {
            return t;
        }
        
        // nothing available - so we need to create or wait
        if (_count.get() < _maxSize) {
            addTransformer();
        }
        return _pool.take();
    }
    
    /**
     * Returns a pooled Transformer instance to the pool.
     * @param transformer pooled transformer
     * @return true if the transformer was added to the pool, false otherwise
     */
    public boolean give(Transformer transformer) {
        return _pool.offer(transformer);
    }
    
    private void addTransformer() throws Exception {
        Transformer transformer = _templates.newTransformer();
        if (_errorListener != null) {
            transformer.setErrorListener(_errorListener);
        }
        _count.incrementAndGet();
        _pool.offer(transformer);
    }
    
}
