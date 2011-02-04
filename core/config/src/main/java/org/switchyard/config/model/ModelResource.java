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
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.config.Configurations;
import org.switchyard.config.Descriptor;
import org.switchyard.config.ElementResource;
import org.switchyard.config.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * ModelResource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ModelResource extends Resource<Model> {

    public static final String MODEL_MARSHALLER = "modelMarshaller";

    private Descriptor _desc;
    private Map<String,ModelMarshaller> _namespace_marshaller_map;

    public ModelResource() {
        this(new Descriptor());
    }

    public ModelResource(Descriptor desc) {
        _desc = desc;
        _namespace_marshaller_map = new HashMap<String,ModelMarshaller>();
    }

    public Descriptor getDescriptor() {
        return _desc;
    }

    @Override
    public Model pull(InputStream is) throws IOException {
        return pull(new ElementResource().pull(is));
    }

    public Model pull(Reader reader) throws IOException {
        return pull(new ElementResource().pull(reader));
    }

    public Model pull(InputSource is) throws IOException {
        return pull(new ElementResource().pull(is));
    }

    public Model pull(Document document) {
        return pull(new ElementResource().pull(document));
    }

    public Model pull(Element element) {
        String namespace = element.getNamespaceURI();
        if (namespace != null) {
            ModelMarshaller marshaller = getModelMarshaller(namespace);
            if (marshaller != null) {
                return marshaller.read(Configurations.create(element));
            }
        }
        return null;
    }

    public Model pull(QName name) {
        return pull(new ElementResource().pull(name));
    }

    public synchronized ModelMarshaller getModelMarshaller(String namespace) {
        ModelMarshaller marshaller = _namespace_marshaller_map.get(namespace);
        if (marshaller == null) {
            String prop_marshaller = _desc.getProperty(MODEL_MARSHALLER, namespace);
            if (prop_marshaller != null) {
                try {
                    Class<?> clazz = Class.forName(prop_marshaller);
                    Constructor<?> cnstr = clazz.getConstructor(Descriptor.class);
                    marshaller = (ModelMarshaller)cnstr.newInstance(_desc);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                _namespace_marshaller_map.put(namespace, marshaller);
            }
        }
        return marshaller;
    }

}
