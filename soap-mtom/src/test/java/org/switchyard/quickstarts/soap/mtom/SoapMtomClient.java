/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

import javax.imageio.ImageIO;
import javax.xml.ws.soap.MTOMFeature;

import org.switchyard.common.type.Classes;

/**
 * Client for SOAP with MTOM.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapMtomClient {

    public static void main(String[] args) throws Exception {
        Image image = sendMessage();
        System.out.println(image);
    }

    public static Image sendMessage() throws Exception {
        ImageService imageService = new ImageServiceService().getImageServicePort(new MTOMFeature());

        // Create the Image and send it to the MTOM endpoint...
        Image img = ImageIO.read(Classes.getResourceAsStream("switchyard_icon.jpeg"));
        if (img == null) {
            throw new IllegalStateException("Failed to load 'switchyard_icon.jpeg' image from classpath!");
        }
        //Image response = imageService.resizeImage(img);
        javax.xml.ws.Holder<Image> holder = new javax.xml.ws.Holder<Image>(img);
        imageService.resizeImage(holder);
        return holder.value;
    }
}
