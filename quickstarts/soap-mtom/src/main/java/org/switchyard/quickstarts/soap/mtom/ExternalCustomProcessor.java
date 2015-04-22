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
package org.switchyard.quickstarts.soap.mtom;

import java.awt.Image;

import javax.activation.DataHandler;
import javax.inject.Named;
import javax.imageio.ImageIO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.common.type.Classes;

@Named("externalCustomProcessor")
public class ExternalCustomProcessor implements Processor {

    /**
     * Creates new processor.
     */
    public ExternalCustomProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String newFileName = "resized-switchyard.jpeg";
        Image input = exchange.getIn().getBody(Image.class);
        if (input == null) {
            throw new RuntimeException("Image for resize not found!");
        }
        Image img = ImageIO.read(Classes.getResourceAsStream("switchyard.jpeg"));
        exchange.getOut().addAttachment(newFileName, new DataHandler(img, "image/jpeg"));
        exchange.getOut().setBody(newFileName);
    }

}
