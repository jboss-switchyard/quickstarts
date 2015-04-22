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
package org.switchyard.component.common.knowledge.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Channel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Channel {

    /** The channel class. */
    public Class<? extends org.kie.api.runtime.Channel> value() default UndefinedChannel.class;

    /** The service reference interface. */
    public Class<?> interfaze() default UndefinedInterface.class;

    /** The channel name. */
    public String name() default "";

    /** The service reference operation. */
    public String operation() default "";

    /** The service reference name. */
    public String reference() default "";

    /** Undefined channel. */
    public static interface UndefinedChannel extends org.kie.api.runtime.Channel {};
    /** Undefined interface. */
    public static interface UndefinedInterface {};

}
