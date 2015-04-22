/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
