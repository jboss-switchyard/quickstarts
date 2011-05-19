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


package org.switchyard.component.camel;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Designates a method as a Camel route provider.  A required value element 
 * of type java.lang.Class is required to identify the service interface for the
 * route service.  A method annotated with <code>@Route</code> must take a 
 * single parameter of <code>ProcessorDefinition</code>, e.g.
 * <br>
 * <pre>
 *     @Route(MyService.class)
 *     public void someRoute(ProcessorDefinition route) {
 *        route.log("here I am!");
 *     }
 * </pre>
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface Route {

    /**
     * Service interface for the route.
     */
    Class<?> value();
}
