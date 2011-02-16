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

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.util.Classes;
import org.switchyard.config.util.ElementResource;
import org.switchyard.config.util.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * ModelResource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ModelResource extends Resource<Model> {

    public static final String MARSHALLER = "marshaller";

    private Descriptor _desc;
    private Map<String,Marshaller> _namespace_marshaller_map;

    public ModelResource() {
        this(null);
    }

    public ModelResource(Descriptor desc) {
        _desc = desc != null ? desc : new Descriptor();
        _namespace_marshaller_map = new HashMap<String,Marshaller>();
    }

    public final Descriptor getDescriptor() {
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
            Marshaller marshaller = getMarshaller(namespace);
            if (marshaller != null) {
                return marshaller.read(Configurations.create(element));
            }
        }
        return null;
    }

    public Model pull(QName name) {
        return pull(new ElementResource().pull(name));
    }

    public synchronized Marshaller getMarshaller(String namespace) {
        Marshaller marshaller = _namespace_marshaller_map.get(namespace);
        if (marshaller == null) {
            String prop_marshaller = _desc.getProperty(MARSHALLER, namespace);
            if (prop_marshaller != null) {
                try {
                    Class<?> clazz = Classes.forName(prop_marshaller, ModelResource.class);
                    Constructor<?> cnstr = clazz.getConstructor(Descriptor.class);
                    marshaller = (Marshaller)cnstr.newInstance(_desc);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                _namespace_marshaller_map.put(namespace, marshaller);
            }
        }
        return marshaller;
    }

    public static Marshaller getMarshaller(Model model) {
        if (model != null) {
            return getMarshaller(model.getModelConfiguration(), model.getModelDescriptor());
        }
        return null;
    }

    public static Marshaller getMarshaller(Configuration config, Descriptor desc) {
        if (config != null) {
            QName qname = config.getQName();
            if (qname != null) {
                String namespace = qname.getNamespaceURI();
                if (namespace != null) {
                    return new ModelResource(desc).getMarshaller(namespace);
                }
            }
        }
        return null;
    }

}
