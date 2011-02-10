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
package org.switchyard.config.util;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * QNames.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class QNames {

    private QNames() {}

    public static QName create(Document document) {
        return create(document.getDocumentElement());
    }

    public static QName create(Element element) {
        return create(element.getNamespaceURI(), Nodes.nameOf(element), element.getPrefix());
    }

    public static QName create(String name) {
        if (name != null) {
            return QName.valueOf(name);
        }
        return null;
    }

    public static QName create(String namespace, String localName) {
        return create(namespace, localName, null);
    }

    public static QName create(String namespace, String localName, String prefix) {
        if (namespace != null && namespace.length() > 0) {
            if (prefix != null && prefix.length() > 0) {
                return new QName(namespace, localName, prefix);
            }
            return new QName(namespace, localName);
        }
        return create(localName);
    }

}
