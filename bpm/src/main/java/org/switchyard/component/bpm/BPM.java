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
import static org.switchyard.component.bpm.process.ProcessResourceType.BPMN2;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.switchyard.component.bpm.process.BaseProcessResource;
import org.switchyard.component.bpm.process.ProcessResource;
import org.switchyard.component.bpm.process.ProcessResourceType;
import org.switchyard.component.bpm.task.BaseTaskHandler;
import org.switchyard.component.bpm.task.TaskHandler;

/**
 * BPM annotation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface BPM {

    /**
     * Specified process interface.
     */
    public Class<?> processInterface() default UndefinedProcessInterface.class;

    /**
     * Specified process definition.
     */
    public String processDefinition() default UNDEFINED_PROCESS_DEFINITION;

    /**
     * Specified process definition type.
     */
    public ProcessResourceType processDefinitionType() default BPMN2;

    /**
     * Specified process id.
     */
    public String processId() default UNDEFINED_PROCESS_ID;

    /**
     * Additional resources the process requires.
     */
    public Class<? extends ProcessResource>[] processResources() default UndefinedProcessResource.class;

    /**
     * Specified task handlers for the process.
     */
    public Class<? extends TaskHandler>[] taskHandlers() default UndefinedTaskHandler.class;

    /** An undefined process interface. */
    public static interface UndefinedProcessInterface {};
    /** The default process interface. */
    public static interface DefaultProcessInterface {
        /**
         * The default process interface process method.
         * @param o in
         * @return out
         */
        public Object process(Object o);
    };
    /** An undefined process definition. */
    public static final String UNDEFINED_PROCESS_DEFINITION = "";
    /** An undefined process id. */
    public static final String UNDEFINED_PROCESS_ID = "";
    /** An undefined process resource. */
    public static final class UndefinedProcessResource extends BaseProcessResource {};
    /** An undefined task handler. */
    public static final class UndefinedTaskHandler extends BaseTaskHandler {};

}
