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

import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Field;

/**
 * SwitchYard test runner.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardRunner extends BlockJUnit4ClassRunner {

    private SwitchYardTestKit _testKit;
    private Object _testInstance;

    /**
     * Creates a SwitchYardRunner to run {@code klass}.
     *
     * @param clazz Test Class.
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public SwitchYardRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Object createTest() throws Exception {
        _testInstance = super.createTest();
        _testKit = new SwitchYardTestKit(_testInstance);

        set(_testKit, PropertyMatchResolution.EQUALS);
        set(_testKit.getDeployment(), PropertyMatchResolution.ASSIGNABLE);
        set(_testKit.getConfigModel(), PropertyMatchResolution.ASSIGNABLE);
        set(_testKit.getServiceDomain(), PropertyMatchResolution.ASSIGNABLE);
        set(_testKit.getServiceDomain().getTransformerRegistry(), PropertyMatchResolution.ASSIGNABLE);

        for (TestMixIn mixIn : _testKit.getMixIns()) {
            set(mixIn, PropertyMatchResolution.EQUALS);
        }

        setInvokers();

        return _testInstance;
    }

    @Override
    public void run(RunNotifier notifier) {
        TestLifecycleListener listener = new TestLifecycleListener();

        notifier.addListener(listener);
        try {
            super.run(notifier);
        } finally {
            notifier.removeListener(listener);
        }
    }

    private class TestLifecycleListener extends RunListener {

        @Override
        public void testFinished(Description description) throws Exception {
            _testKit.cleanup();
        }
    }

    private enum PropertyMatchResolution {
        ASSIGNABLE,
        EQUALS
    }

    private void set(Object propertyValue, PropertyMatchResolution matchRes) throws IllegalAccessException {
        Field[] fields = _testInstance.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (matchRes == PropertyMatchResolution.ASSIGNABLE) {
                if (field.getType().isInstance(propertyValue)) {
                    set(field, propertyValue);
                }
            } else if (matchRes == PropertyMatchResolution.EQUALS) {
                if (field.getType() == propertyValue.getClass()) {
                    set(field, propertyValue);
                }
            }
        }
    }

    private void set(Field field, Object propertyValue) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(_testInstance, propertyValue);
        } finally {
            field.setAccessible(accessible);
        }
    }

    private void setInvokers() throws IllegalAccessException {
        Field[] fields = _testInstance.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == Invoker.class) {
                ServiceOperation serviceOp = field.getAnnotation(ServiceOperation.class);

                if (serviceOp == null) {
                    Assert.fail("Invoker property '" + field.getName() + "' on test class '" + _testInstance.getClass().getName() + "' needs to be annotated with a @ServiceOperation.");
                }

                Invoker invoker = _testKit.newInvoker(serviceOp.value());

                if (invoker == null) {
                    Assert.fail("Invoker property '" + field.getName() + "' on test class '" + _testInstance.getClass().getName() + "' contains an a @ServiceOperation defining an unknown Service Operation value '" + serviceOp.value() + "'.");
                }

                set(field, invoker);
            }
        }
    }
}
