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

import org.switchyard.config.model.Scanner;
import org.switchyard.deploy.internal.AbstractDeployment;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * SwitchYard test deployment configuration annotation.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface SwitchYardTestCaseConfig {

    /**
     * Default classpath location for the switchyard configuration.
     */
    public static final String SWITCHYARD_XML = AbstractDeployment.SWITCHYARD_XML;

    /**
     * Classpath path to a switchyard.xml configuration.
     */
    String config() default SwitchYardTestCase.NULL_CONFIG;

    /**
     * {@link Scanner Scanners} to be used in the test.
     * <p/>
     * These are the same application scanners used by the SwitchYard maven plugin.  The
     * augment the configuration model pointed to by the {@link #config()} value.  The scanners
     * are only applied if a {@link #config()} is specified.
     */
    Class<? extends Scanner>[] scanners() default SwitchYardTestCase.NullScanners.class;

    /**
     * The Mix in types.
     */
    Class<? extends TestMixIn>[] mixins() default SwitchYardTestCase.NullMixIns.class;
}
