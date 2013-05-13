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
package org.switchyard.quickstarts.camel.jaxb;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * Utility class for JAXB related operations.
 */
public class JAXBUtil {

    private static JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(JAXBUtil.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private JAXBUtil() {
    }

    /**
     * Returns string representation of {@link GreetingRequest}.
     * 
     * @param rq Request.
     * @return XML representation.
     * @throws JAXBException
     */
    public static String marshal(GreetingRequest rq) throws JAXBException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Marshaller marshaller = CONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(rq, bos);
        return new String(bos.toByteArray());
    }

    /**
     * Returns object representation of XML.
     * 
     * @param str XML representation.
     * @return {@link GreetingResponse} instance.
     * @throws JAXBException
     */
    @SuppressWarnings("rawtypes")
    public static GreetingResponse unmarshal(String str) throws JAXBException {
        JAXBElement unmarshal = (JAXBElement) CONTEXT.createUnmarshaller().unmarshal(new ByteArrayInputStream(str.getBytes()));
        return (GreetingResponse) unmarshal.getValue();
    }

}
