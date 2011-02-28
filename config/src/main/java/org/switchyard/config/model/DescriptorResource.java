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
package org.switchyard.config.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.switchyard.config.util.PropertiesResource;
import org.switchyard.config.util.Resource;

/**
 * Utility class to safely access ("pull") Descriptors from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DescriptorResource extends Resource<Descriptor> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Descriptor pull(InputStream is) throws IOException {
        return pull(new PropertiesResource().pull(is));
    }

    /**
     * Safely pulls a Descriptor from a Reader.
     * @param reader a Reader of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public Descriptor pull(Reader reader) throws IOException {
        return pull(new PropertiesResource().pull(reader));
    }

    /**
     * Safely pulls a Descriptor from Properties.
     * @param props Properties of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public Descriptor pull(Properties props) throws IOException {
        return new Descriptor(props);
    }

    /**
     * Creates a default Descriptor.
     * @return the resource, never null
     * @throws IOException shouldn't happen, but might ;)
     */
    public Descriptor pull() throws IOException {
        return new Descriptor();
    }

}
