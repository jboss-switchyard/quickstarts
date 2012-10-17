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
package org.switchyard.test.mixins;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * Helper interface which lets abstract {@link JTAEnvironmentBean} lookup.
 */
public interface TransactionMixInParticipant {

    /**
     * Locates Arjuna environment bean, if null value is returned then
     * TransactionMixIn will continue looking it in next participant. If all
     * participants will return null value then default JTAEnvironmentBean will
     * be used.
     * 
     * @return Instance of environment bean or null.
     * @throws Throwable In case of failure to look up.
     */
    JTAEnvironmentBean locateEnvironmentBean() throws Throwable;

}
