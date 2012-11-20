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
package org.switchyard.component.bpm.task;

import java.util.Map;

/**
 * Base Content functionality.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class BaseTaskContent implements TaskContent {

    private Map<String, Object> _variableMap;

    /**
     * Constructs a new BaseTaskContent with the specified variable map.
     * @param variableMap the specified variable map (cannot be null!)
     */
    public BaseTaskContent(Map<String, Object> variableMap) {
        if (variableMap == null) {
            throw new IllegalArgumentException("variable map cannot be null");
        }
        _variableMap = variableMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getVariableMap() {
        return _variableMap;
    }

}
