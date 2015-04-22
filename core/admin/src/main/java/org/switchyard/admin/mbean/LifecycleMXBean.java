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
