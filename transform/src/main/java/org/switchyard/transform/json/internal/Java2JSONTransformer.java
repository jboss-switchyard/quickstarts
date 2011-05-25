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

package org.switchyard.transform.json.internal;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.transform.BaseTransformer;

/**
 * Java to JSON Transformer.
 *
 * @author Alejandro Montenegro &lt;<a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
public class Java2JSONTransformer extends BaseTransformer {

    private ObjectMapper _mapper;
    private Class _clazz;

    /**
     * Public constructor.
     *
     * @param from   From type.
     * @param to     To type.
     * @param mapper JSON Object Mapper instance.
     * @param clazz  The Java type being mapped.
     */
    public Java2JSONTransformer(QName from, QName to, ObjectMapper mapper, Class clazz) {
        super(from, to);
        this._mapper = mapper;
        this._clazz = clazz;
    }

    @Override
    public Object transform(Object from) {

        try {
            StringWriter writer = new StringWriter();

            if (_clazz.isInstance(from)) {
                _mapper.writeValue(writer, from);
                return writer.toString();
            } else {
                throw new RuntimeException("The object to transform is of wrong instance type " + from.getClass());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unexpected JSON processing exception, check your transformer configuration", e);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected I/O exception, check your transformer configuration", e);
        }
    }
}
