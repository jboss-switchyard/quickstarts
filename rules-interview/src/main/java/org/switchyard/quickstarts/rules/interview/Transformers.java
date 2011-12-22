/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.rules.interview;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Transformers {

    // Element names in XML document
    private static final String NAME = "name";
    private static final String AGE = "age";

    /**
     * Transform from a DOM element to an Applicant instance.
     * <p/>
     * No need to specify the "to" type because Applicant is a concrete type.
     * @param from Applicant element.
     * @return Applicant instance.
     */
    @Transformer(from = "{urn:switchyard-quickstart:rules-interview:0.1.0}applicant")
    public Applicant transform(Element from) {
        Applicant applicant = new Applicant(getElementValue(from, NAME), Integer.parseInt(getElementValue(from, AGE)));

        return applicant;
    }

    /**
     * Returns the text value of a child node of parent.
     */
    private String getElementValue(Element parent, String elementName) {
        String value = null;
        NodeList nodes = parent.getElementsByTagName(elementName);
        if (nodes.getLength() > 0) {
            value = nodes.item(0).getChildNodes().item(0).getNodeValue();
        }
        return value;
    }
}
