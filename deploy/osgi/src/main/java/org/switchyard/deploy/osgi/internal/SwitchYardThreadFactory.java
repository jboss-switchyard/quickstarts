/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.deploy.osgi.internal;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SwitchYardThreadFactory.
 */
public class SwitchYardThreadFactory implements ThreadFactory {

    private final ThreadFactory _factory = Executors.defaultThreadFactory();
    private final AtomicInteger _count = new AtomicInteger();
    private final String _name;

    /**
     * Create a new instance of SwitchYardThreadFactory.
     * @param name
     */
    public SwitchYardThreadFactory(String name) {
        _name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread t = _factory.newThread(r);
        t.setName(_name + ": " + _count.incrementAndGet());
        t.setDaemon(true);
        return t;
    }

}
