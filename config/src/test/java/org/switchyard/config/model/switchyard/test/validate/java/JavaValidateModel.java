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

package org.switchyard.config.model.switchyard.test.validate.java;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;

/**
 * JavaTransformModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JavaValidateModel extends V1BaseValidateModel {

    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:test-validate-java:1.0";
    public static final String JAVA = "java";
    public static final String CLASS = "class";

    public JavaValidateModel() {
        super(new QName(DEFAULT_NAMESPACE, ValidateModel.VALIDATE + '.' + JAVA));
    }

    public JavaValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public String getClazz() {
        return getModelAttribute(CLASS);
    }

    public JavaValidateModel setClazz(String clazz) {
        setModelAttribute(CLASS, clazz);
        return this;
    }

}
