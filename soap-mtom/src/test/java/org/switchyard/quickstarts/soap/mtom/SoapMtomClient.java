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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.MTOMFeature;

import org.switchyard.common.type.Classes;

/**
 * Client for SOAP with MTOM.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapMtomClient {

    private static String WSDL = "http://localhost:8080/soap-mtom/ImageServiceService?wsdl";

    public static void main(String[] args) throws Exception {
        Image image = sendMessage();
        System.out.println(image);
    }

    public static Image sendMessage() throws Exception {
        ImageService imageService = new ImageServiceService(new URL(WSDL)).getImageServicePort(new MTOMFeature());

        // Create the Image bytes and send it to the MTOM endpoint...
        URL fileURL = Classes.getResource("switchyard_icon.jpeg");
        File aFile = new File(new URI(fileURL.toString()));
        long fileSize = aFile.length();

        Holder<byte[]> param = new Holder<byte[]>();
        param.value = new byte[(int) fileSize];
        InputStream in = fileURL.openStream();
        int len = in.read(param.value);
        while (len < fileSize) {
            len += in.read(param.value, len, (int)(fileSize - len));
        }
        byte[] response = imageService.resizeImage(param);
        return ImageIO.read(new ByteArrayInputStream(response));
    }
}
