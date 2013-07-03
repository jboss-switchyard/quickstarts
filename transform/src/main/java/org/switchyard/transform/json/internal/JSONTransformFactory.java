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

package org.switchyard.transform.json.internal;

import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.SwitchYardException;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.JSONTransformModel;
import org.switchyard.transform.internal.TransformerFactory;

/**
 * JSON Transformer factory.
 *
 * @author Alejandro Montenegro &lt;<a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
public final class JSONTransformFactory implements TransformerFactory<JSONTransformModel> {

    /**
     * Create a {@link Transformer} instance from the supplied {@link JSONTransformModel}.
     * @param model the JSON transformer model. 
     * @return the Transformer instance.
     */
    public Transformer newTransformer(JSONTransformModel model) {

        QName from = model.getFrom();
        QName to = model.getTo();

        assertValidJSONTransformSpec(from, to);

        if (QNameUtil.isJavaMessageType(from)) {
            // Java to JSON....
            Class clazz = toJavaMessageType(from);
            return new Java2JSONTransformer(from, to, new ObjectMapper(), clazz);
        } else {
            // JSON to Java....
            Class clazz = toJavaMessageType(to);
            return new JSON2JavaTransformer(from, to, new ObjectMapper(), clazz);
        }
    }

    private static Class toJavaMessageType(QName name) {
        Class clazz = QNameUtil.toJavaMessageType(name);
        if (clazz == null) {
            throw new SwitchYardException("Not able to find class definition " + name);
        }
        return clazz;
    }

    private static void assertValidJSONTransformSpec(QName from, QName to) {
        if (QNameUtil.isJavaMessageType(from)) {
            if (QNameUtil.isJavaMessageType(to)) {
                // Both of them is a Java type spec...
                throwInvalidToFromSpecException();
            }
        } else if (QNameUtil.isJavaMessageType(to)) {
            if (QNameUtil.isJavaMessageType(from)) {
                // Both of them is a Java type spec...
                throwInvalidToFromSpecException();
            }
        } else {
            // Neither of them is a Java type spec...
            throwInvalidToFromSpecException();
        }
    }

    private static void throwInvalidToFromSpecException() {
        throw new SwitchYardException("Invalid JSON Transformer configuration.  One (and only one) of the specified 'to' and 'from' transform types must be a Java type.");
    }
}
