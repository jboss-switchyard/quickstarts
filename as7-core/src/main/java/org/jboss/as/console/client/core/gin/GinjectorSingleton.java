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
package org.jboss.as.console.client.core.gin;

/**
 * GinjectorSingleton
 * 
 * <p/>
 * Simple API which uses deferred binding to initialize the application
 * Ginjector. This can be implemented by console aggregators to provide an
 * extended CoreUI Ginjector (i.e. a Ginjector that includes extended
 * presentation bindings in addition to CoreUI).
 * 
 * <p/>
 * For example:
 * 
 * <pre>
 * <code>
 * private static final CoreUI instance = GWT.create(CoreUI.class);
 * 
 * public CoreUI getCoreUI() {
 *    return instance;
 * }
 * </code>
 * </pre>
 * 
 * @author Rob Cernich
 */
public interface GinjectorSingleton {

    /**
     * @return the CoreUI Ginjector.
     */
    CoreUI getCoreUI();
}
