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
 
package org.switchyard.component.resteasy.resource;

import java.util.ServiceLoader;

import org.switchyard.component.resteasy.RESTEasyPublishException;

/**
 * Factory for creating Resource publisher.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ResourcePublisherFactory {

    private static final ResourcePublisher PUBLISHER;


    private ResourcePublisherFactory() {
    }

    /**
     * Creates a ResourcePublisher based on the environment.
     * @return The ResourcePublisher
     */
    public static ResourcePublisher getPublisher() {
        return PUBLISHER;
    }

    static {
        try {
            PUBLISHER = ServiceLoader.load(ResourcePublisher.class).iterator().next();
        } catch (Exception e) {
            throw new RESTEasyPublishException(e);
        }
    }
}
