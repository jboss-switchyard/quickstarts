/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.component.test.mixins.cdi;

import org.jboss.weld.bootstrap.spi.Deployment;

/**
 * Interface for MixIns which are interested in populating Weld deployment during
 * startup of CDI MixIn.
 */
public interface CDIMixInParticipant {

    /**
     * Method called after Weld SE deployment is created.
     * 
     * @param deployment Weld deployment instance.
     * @throws Exception If something goes wrong and CDI environment can not be
     * populated with necessary beans.
     */
    void participate(Deployment deployment) throws Exception;


}
