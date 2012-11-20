/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.rules.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.switchyard.component.common.knowledge.annotation.Channel;
import org.switchyard.component.common.knowledge.annotation.Listener;
import org.switchyard.component.common.knowledge.annotation.Logger;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Property;

/**
 * Rules.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Rules {

    /** Name. */
    public String name() default "";

    /** Interface class. */
    public Class<?> value() default UndefinedRulesInterface.class;

    /** Channels. */
    public Channel[] channels() default {};

    /** Listeners. */
    public Listener[] listeners() default {};

    /** Loggers. */
    public Logger[] loggers() default {};

    /** Manifest. */
    public Manifest[] manifest() default {};

    /** Properties. */
    public Property[] properties() default {};

    /** Undefined interface class. */
    public static interface UndefinedRulesInterface {};

}
