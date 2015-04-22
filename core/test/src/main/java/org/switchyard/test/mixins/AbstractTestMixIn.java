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

package org.switchyard.test.mixins;

import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.TestMixIn;

/**
 * Abstract {@link TestMixIn}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractTestMixIn implements TestMixIn {

    private SwitchYardTestKit _kit;

    @Override
    public void setTestKit(SwitchYardTestKit kit) {
        this._kit = kit;
    }

    /**
     * Get the test kit instance associated with the test mixin.
     * @return The test kit instance instance.
     */
    public SwitchYardTestKit getTestKit() {
        return _kit;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void before(AbstractDeployment deployment) {
    }

    @Override
    public void after(AbstractDeployment deployment) {
    }

    @Override
    public void uninitialize() {
    }
}
