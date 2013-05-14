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
package org.switchyard.common.property;

import java.util.Map;

/**
 * Resolves properties from a Map.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class MapPropertyResolver implements PropertyResolver {

    private final Map<String, Object> _map;

    /**
     * Construction with the specified Map.
     * @param map the specified Map
     */
    public MapPropertyResolver(Map<String, Object> map) {
        _map = map;
    }

    /**
     * Gets the Map.
     * @return the Map
     */
    public Map<String, Object> getMap() {
        return _map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object resolveProperty(String key) {
        return key != null ? _map.get(key) : null;
    }

}
