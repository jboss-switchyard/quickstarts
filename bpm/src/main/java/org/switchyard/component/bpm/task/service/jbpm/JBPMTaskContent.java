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
package org.switchyard.component.bpm.task.service.jbpm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.task.Content;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.utils.MarshalledContentWrapper;
import org.switchyard.common.type.Classes;
import org.switchyard.component.bpm.task.service.BaseTaskContent;
import org.switchyard.exception.SwitchYardException;

/**
 * A jBPM TaskContent implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class JBPMTaskContent extends BaseTaskContent {

    /**
     * Creates a new JBPMTaskContent with the specified Content.
     * @param content the specified Content
     */
    public JBPMTaskContent(Content content) {
        this(content.getContent());
    }

    /**
     * Creates a new JBPMTaskContent with the specified ContentData.
     * @param contentData the specified ContentData.
     */
    public JBPMTaskContent(ContentData contentData) {
        this(contentData.getContent());
    }

    /**
     * Creates a new JBPMTaskContent with the specified bytes.
     * @param bytes the specified bytes.
     */
    public JBPMTaskContent(byte[] bytes) {
        setObject(readObject(readBytes(bytes)));
    }

    private Object readObject(Object object) {
        if (object != null) {
            if (object instanceof Collection) {
                object = readCollection((Collection<?>)object);
            } else if (object instanceof Map) {
                object = readMap((Map<?, ?>)object);
            } else if (object.getClass().isArray()) {
                object = readArray((Object[])object);
            } else if (object instanceof MarshalledContentWrapper) {
                object = readBytes(((MarshalledContentWrapper)object).getContent());
            }
        }
        return object;
    }

    private Collection<?> readCollection(Collection<?> collection) {
        Collection<Object> c = new ArrayList<Object>();
        for (Object o : collection) {
            c.add(readObject(o));
        }
        return collection;
    }

    private Map<Object, Object> readMap(Map<?, ?> map) {
        Map<Object, Object> m = new LinkedHashMap<Object, Object>();
        for (Entry<?, ?> entry : map.entrySet()) {
            m.put(entry.getKey(), readObject(entry.getValue()));
        }
        return m;
    }

    private Object[] readArray(Object[] array) {
        Object[] a = new Object[array.length];
        for (int i=0; i < a.length; i++) {
            a[i] = readObject(array[i]);
        }
        return a;
    }

    private Object readBytes(byte[] bytes) {
        Object object = null;
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

}
