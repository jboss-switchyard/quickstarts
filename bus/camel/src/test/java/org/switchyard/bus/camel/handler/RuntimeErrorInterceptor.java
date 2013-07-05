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
package org.switchyard.bus.camel.handler;

import java.util.Arrays;
import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.HandlerException;

/**
 * Simple handler throwing exception during IN phase.
 */
public class RuntimeErrorInterceptor implements ExchangeInterceptor {
    
    private boolean waitForAfter;
    private String target;
    private int count;
    
    public RuntimeErrorInterceptor(boolean waitForAfter, String target) {
        this.waitForAfter = waitForAfter;
        this.target = target;
    }

    @Override
    public void before(String target, Exchange exchange) throws HandlerException {
        ++count;
        if (!waitForAfter) {
            throw new RuntimeException("RuntimeException before on target " + target);
        }
    }

    @Override
    public void after(String target, Exchange exchange) throws HandlerException {
        ++count;
        if (waitForAfter) {
            throw new RuntimeException("RuntimeException after on target " + target);
        }
    }

    @Override
    public List<String> getTargets() {
        return Arrays.asList(target);
    }

    public int getCount() {
        return count;
    }

}
