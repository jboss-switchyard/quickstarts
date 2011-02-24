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

import javax.xml.namespace.QName;

import org.switchyard.config.ConfigurationResource;
import org.switchyard.config.util.ElementResource;
import org.switchyard.config.util.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * ModelResource.
 *
 * @param <M> the Model type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ModelResource<M extends Model> extends Resource<M> {

    private Descriptor _desc;

    public ModelResource() {
        this(null);
    }

    public ModelResource(Descriptor desc) {
        _desc = desc != null ? desc : new Descriptor();
    }

    public final Descriptor getDescriptor() {
        return _desc;
    }

    @Override
    public M pull(InputStream is) throws IOException {
        return pull(new ElementResource().pull(is));
    }

    public M pull(Reader reader) throws IOException {
        return pull(new ElementResource().pull(reader));
    }

    public M pull(InputSource is) throws IOException {
        return pull(new ElementResource().pull(is));
    }

    public M pull(Document document) {
        return pull(new ElementResource().pull(document));
    }

    @SuppressWarnings("unchecked")
    public M pull(Element element) {
        String namespace = element.getNamespaceURI();
        if (namespace != null) {
            Marshaller marshaller = _desc.getMarshaller(namespace);
            if (marshaller != null) {
                return (M)marshaller.read(new ConfigurationResource().pull(element));
            }
        }
        return null;
    }

    public M pull(QName name) {
        return pull(new ElementResource().pull(name));
    }

}
