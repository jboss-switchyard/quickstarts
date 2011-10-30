/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.switchyard.component.bpm.task.BaseTaskHandler;
import org.switchyard.component.bpm.task.TaskHandler;

/**
 * Process annotation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Process {

    /**
     * Specified process interface.
     */
    public Class<?> value() default UndefinedProcessInterface.class;

    /**
     * Specified process definition.
     */
    public String definition() default UNDEFINED_PROCESS_DEFINITION;

    /**
     * Specified process definition type.
     */
    public String definitionType() default BPMN2;

    /**
     * Specified process id.
     */
    public String id() default UNDEFINED_PROCESS_ID;

    /**
     * Specified agent flag.
     */
    public boolean agent() default false;

    /**
     * Specified message content in name.
     */
    public String messageContentInName() default UNDEFINED_MESSAGE_CONTENT_NAME;

    /**
     * Specified message content out name.
     */
    public String messageContentOutName() default UNDEFINED_MESSAGE_CONTENT_NAME;

    /**
     * Additional resources the process requires.
     */
    public String[] resources() default UNDEFINED_RESOURCE;

    /**
     * Specified task handlers for the process.
     */
    public Class<? extends TaskHandler>[] taskHandlers() default UndefinedTaskHandler.class;

    /** An undefined process interface. */
    public static interface UndefinedProcessInterface {};
    /** An undefined process definition. */
    public static final String UNDEFINED_PROCESS_DEFINITION = "";
    /** The BPMN2 ResourceType name . */
    public static final String BPMN2 = "BPMN2";
    /** An undefined process id. */
    public static final String UNDEFINED_PROCESS_ID = "";
    /** An undefined message content name. */
    public static final String UNDEFINED_MESSAGE_CONTENT_NAME = "";
    /** An undefined process resource. */
    public static final String UNDEFINED_RESOURCE = "";
    /** An undefined task handler. */
    public static final class UndefinedTaskHandler extends BaseTaskHandler {};

}
