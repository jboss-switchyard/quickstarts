/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.test;

import org.switchyard.deploy.internal.AbstractDeployment;

/**
 * Test Mix-In.
 * <p/>
 * Test Mix-Ins are used to extend the behavior of a {@link SwitchYardTestCase} implementation.
 * See the {@link org.switchyard.test.mixins} package for a list of the {@link TestMixIn TestMixIns}
 * available out of the box.
 * <p/>
 * Test Mix-Ins have a lifecycle associated with your TestCase test methods.  The are created
 * and destroyed for each test method (<i>@Before</i> and <i>@After</i>).  See the javadoc on each of the
 * methods of this class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface TestMixIn {

    /**
     * Set the test case instance on the {@link TestMixIn}.
     * @param testCase The test case.
     */
    void setTestCase(SwitchYardTestCase testCase);

    /**
     * MixIn setUp.
     * <p/>
     * Performed before the Test Deployment is created.
     */
    void setUp();

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
     * MixIn tear down.
     * <p/>
     * Performed after test method execution and after the deployment is destroyed.
     */
    void tearDown();
}
