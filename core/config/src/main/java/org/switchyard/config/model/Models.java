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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.Descriptor;
import org.switchyard.config.QNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Models {

    public static final String DEFAULT_NAMESPACE = "http://docs.oasis-open.org/ns/opencsa/sca/200912";

    private Models() {}

    public static Model create(Document document) {
        return new ModelResource().pull(document);
    }

    public static Model create(Document document, Descriptor desc) {
        return new ModelResource(desc).pull(document);
    }

    public static Model create(Element element) {
        return new ModelResource().pull(element);
    }

    public static Model create(Element element, Descriptor desc) {
        return new ModelResource(desc).pull(element);
    }

    public static Model create(QName qname) {
        return new ModelResource().pull(qname);
    }

    public static Model create(QName qname, Descriptor desc) {
        return new ModelResource(desc).pull(qname);
    }

    public static Model create(String name) {
        return create(DEFAULT_NAMESPACE, name, null);
    }

    public static Model create(String name, Descriptor desc) {
        return create(DEFAULT_NAMESPACE, name, null, desc);
    }

    public static Model create(String namespace, String localName, String prefix) {
        return create(QNames.create(namespace, localName, prefix));
    }

    public static Model create(String namespace, String localName, String prefix, Descriptor desc) {
        return create(QNames.create(namespace, localName, prefix), desc);
    }

    public static Model merge(Model fromModel, Model toModel) {
        return merge(fromModel, toModel, true);
    }

    public static Model merge(Model fromModel, Model toModel, boolean fromOverridesTo) {
        String from_model_cn = fromModel.getClass().getName();
        String to_model_cn = toModel.getClass().getName();
        if (!from_model_cn.equals(to_model_cn)) {
            throw new IllegalArgumentException(from_model_cn + " != " + to_model_cn);
        }
        Configuration from_model_config = fromModel.getModelConfiguration();
        Configuration to_model_config = toModel.getModelConfiguration();
        Configuration merged_model_config = Configurations.merge(from_model_config, to_model_config, fromOverridesTo);
        Model merged_model = toModel.getModelMarshaller().read(merged_model_config);
        return merged_model;
    }

}
