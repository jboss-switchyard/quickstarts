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
