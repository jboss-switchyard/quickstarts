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
    String SWITCHYARD_XML = AbstractDeployment.SWITCHYARD_XML;
    /**
     * Constant for the {@link SwitchYardTestCaseConfig#config()} default.
     */
    String NULL_CONFIG = "$$NULL_SW_CONFIG$$";

    /**
     * Classpath path to a switchyard.xml configuration.
     */
    String config() default NULL_CONFIG;

    /**
     * Whether validating SwitchYard configuration model or not.
     */
    boolean validate() default true;

    /**
     * {@link Scanner Scanners} to be used in the test.
     * <p/>
     * These are the same application scanners used by the SwitchYard maven plugin.  The
     * augment the configuration model pointed to by the {@link #config()} value.  The scanners
     * are only applied if a {@link #config()} is specified.
     */
    Class<? extends Scanner>[] scanners() default SwitchYardTestKit.NullScanners.class;

    /**
     * The Mix in types.
     */
    Class<? extends TestMixIn>[] mixins() default SwitchYardTestKit.NullMixIns.class;

    /**
     * Component types to include.
     */
    String[] include() default {};

    /**
     * Component types to exclude.
     */
    String[] exclude() default {};
}
