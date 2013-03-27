/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.util;

import org.switchyard.Context;
import org.switchyard.Scope;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelContextMapper;

public class Mapper extends CamelContextMapper {

    public static final String PROPERTY = "TestProperty";
    public static final String VALUE = "TestValue";

    @Override
    public void mapFrom(CamelBindingData source, Context context) throws Exception {
        super.mapFrom(source, context);
        context.setProperty(PROPERTY, VALUE);
    }

    @Override
    public void mapTo(Context context, CamelBindingData target) throws Exception {
        super.mapTo(context, target);
        target.getMessage().setHeader(PROPERTY, VALUE);
    }

}
