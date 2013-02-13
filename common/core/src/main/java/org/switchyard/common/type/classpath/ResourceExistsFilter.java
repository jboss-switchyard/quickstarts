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

package org.switchyard.common.type.classpath;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ResourceExistsFilter implements Filter {

    private String _resourceName;
    private boolean _resourceFound = false;

    /**
     * Public constructor.
     * @param resourceName The name of the resource to be checked for.
     */
    public ResourceExistsFilter(String resourceName) {
        this._resourceName = resourceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(String resourceName) {
        _resourceFound = resourceName.equals(_resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean continueScanning() {
        return !_resourceFound;
    }

    /**
     * Was the resource was found on the scan.
     * @return True if the resource was found, otherwise false.
     */
    public boolean resourceExists() {
        return _resourceFound;
    }
}
