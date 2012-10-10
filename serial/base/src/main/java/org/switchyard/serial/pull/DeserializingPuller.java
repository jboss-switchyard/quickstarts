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
package org.switchyard.serial.pull;

import java.io.IOException;
import java.io.InputStream;

import org.switchyard.common.io.pull.Puller;
import org.switchyard.serial.Serializer;

/**
 * Utility class to safely deserialize ("pull") Objects from various sources.
 * 
 * @param <T> the type of object
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class DeserializingPuller<T> extends Puller<T> {

    private final Serializer _serializer;
    private final Class<T> _type;

    /**
     * Constructor with a serializer and type.
     * @param serializer the serializer
     * @param type the type
     */
    public DeserializingPuller(Serializer serializer, Class<T> type) {
        _serializer = serializer;
        _type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T pull(InputStream stream) throws IOException {
        return _serializer.deserialize(stream, _type);
    }

}
