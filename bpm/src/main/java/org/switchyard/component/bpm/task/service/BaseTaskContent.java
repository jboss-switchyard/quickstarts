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
package org.switchyard.component.bpm.task.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.Map;

import org.switchyard.common.type.Classes;
import org.switchyard.exception.SwitchYardException;

/**
 * Base Content functionality.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseTaskContent implements TaskContent {

    private Long _id;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskContent setId(Long id) {
        _id = id;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject() {
        Object object = null;
        byte[] bytes = getBytes();
        if (bytes != null && bytes.length > 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais) {
                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        Class<?> clazz = Classes.forName(desc.getName(), getClass());
                        return clazz != null ? clazz : super.resolveClass(desc);
                    }
                };
                object = ois.readObject();
            } catch (IOException ioe) {
                throw new SwitchYardException(ioe);
            } catch (ClassNotFoundException cnfe) {
                throw new SwitchYardException(cnfe);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ioe) {
                        throw new SwitchYardException(ioe);
                    }
                }
            }
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(Class<T> clazz) {
        return clazz.cast(getObject());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskContent setObject(Object object) {
        byte[] bytes = null;
        if (object != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                oos.flush();
            } catch (IOException ioe) {
                throw new SwitchYardException(ioe);
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException ioe) {
                        throw new SwitchYardException(ioe);
                    }
                }
            }
            bytes = baos.toByteArray();
        }
        setBytes(bytes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap() {
        return (Map<String, Object>)getObject(Map.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskContent setMap(Map<String, Object> map) {
        setObject(map);
        return this;
    }

}
