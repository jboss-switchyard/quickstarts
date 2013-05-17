/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.quickstarts.rest.binding;

import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.core.BaseClientResponse;
import org.switchyard.annotations.Transformer;

@Named("Transformers")
public class Transformers {

    /**
     * Transform from a RESTEasy Response to an Item instance.
     * <p/>
     * No need to specify the "to" type because Item is a concrete type.
     * @param from Response object.
     * @return Item instance.
     */
    @Transformer(from = "java:org.jboss.resteasy.client.core.BaseClientResponse")
    public Item transform(BaseClientResponse from) {
        from.setReturnType(Item.class);
        return (Item)from.getEntity();
    }
}
