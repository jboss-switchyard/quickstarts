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
    Composite getCoreUI();
}
