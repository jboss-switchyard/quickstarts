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
package org.switchyard.remote.cluster;

import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteEndpoint;

/**
 * Random endpoint selection strategy which uses a Java Random with default seed to select an 
 * available endpoint in the list.
 */
public class RandomStrategy extends BaseStrategy {
    
    private Random _random = new Random();
    
    /**
     * Create a new RandomStrategy.
     */
    public RandomStrategy() {
        super();
    }

    @Override
    public RemoteEndpoint selectEndpoint(QName serviceName) {
        if (getRegistry() == null) {
            return null;
        }
        
        RemoteEndpoint selectedEp = null;
        List<RemoteEndpoint> eps = getRegistry().getEndpoints(serviceName);
        if (!eps.isEmpty()) {
            int idx = _random.nextInt(Integer.MAX_VALUE) % eps.size();
            selectedEp = eps.get(idx);
        }
        
        return selectedEp;
    }

}
