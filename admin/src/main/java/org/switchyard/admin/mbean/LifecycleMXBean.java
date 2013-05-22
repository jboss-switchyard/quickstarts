/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.admin.mbean;

/**
 * LifecycleMXBean
 * <p/>
 * Common operations for manipulating object lifecycle.
 */
public interface LifecycleMXBean {

    /**
     * Represents the current state of a Lifecycle object.
     */
    public enum State {
        /** No state. */
        NONE,
        /**
         * In the process of starting, i.e. start() has been invoked, but has
         * not yet completed.
         */
        STARTING,
        /**
         * The object has been started and is running, i.e. start() has been
         * invoked and has successfully completed.
         */
        STARTED,
        /**
         * In the process of stopping, i.e. stop() has been invoked, but has not
         * yet completed.
         */
        STOPPING;
    }

    /**
     * Start processing.
     */
    void start();

    /**
     * Stop processing.
     */
    void stop();

    /**
     * @return the current state of this object.
     */
    State getState();
}
