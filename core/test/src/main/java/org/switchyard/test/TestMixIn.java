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

package org.switchyard.test;

import org.switchyard.deploy.internal.AbstractDeployment;

/**
 * Test Mix-In.
 * <p/>
 * Test Mix-Ins are used to extend the behavior of {@link SwitchYardTestKit}.
 * See the {@link org.switchyard.test.mixins} package for a list of the {@link TestMixIn TestMixIns}
 * available out of the box.
 * <p/>
 * Test Mix-Ins have a lifecycle associated with your TestCase.  They are created
 * and destroyed with the TestCase instance and the {@link #before(org.switchyard.deploy.internal.AbstractDeployment)}
 * and {@link #after(org.switchyard.deploy.internal.AbstractDeployment)} methods are called <i>@Before</i> and <i>@After</i>)
 * each test method.  See the javadoc on each of the methods of this class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface TestMixIn {

    /**
     * Set the test kit instance on the MixIn.
     * @param kit The kit instance.
     */
    void setTestKit(SwitchYardTestKit kit);

    /**
     * MixIn initialization.
     * <p/>
     * Performed on test construction.
     */
    void initialize();

    /**
     * Before test method execution (init).
     * @param deployment The deployment.
     */
    void before(AbstractDeployment deployment);

    /**
     * After test method execution (cleanup).
     * <p/>
     * Performed before the deployment is destroyed.
     *
     * @param deployment The deployment.
     */
    void after(AbstractDeployment deployment);

    /**
     * MixIn uninitialize.
     * <p/>
     * Performed after TesCase TestRunner has finished running all the TestCase test methods.
     */
    void uninitialize();

}
