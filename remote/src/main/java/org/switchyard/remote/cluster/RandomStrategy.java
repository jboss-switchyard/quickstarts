/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.remote.cluster;

import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;

/**
 * Random endpoint selection strategy which uses a Java Random with default seed to select an 
 * available endpoint in the list.
 */
public class RandomStrategy extends BaseStrategy {
    
    private Random _random = new Random();
    
    /**
     * Create a new RandomStrategy.
     * @param registry remote registry
     */
    public RandomStrategy(RemoteRegistry registry) {
        super(registry);
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
