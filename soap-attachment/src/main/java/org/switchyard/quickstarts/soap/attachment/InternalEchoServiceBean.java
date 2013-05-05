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

package org.switchyard.quickstarts.soap.attachment;

import javax.activation.DataSource;
import javax.inject.Inject;

import org.switchyard.Context;
import org.switchyard.Message;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(InternalEchoService.class)
public class InternalEchoServiceBean implements InternalEchoService {

    @Inject
    private Message message;

    @Inject @Reference
    private EchoService _echoService;

    @Override
    public String echoImage(String fileName) {
        String newFileName = "external-switchyard.png";
        DataSource image = message.getAttachment(fileName);
        // Something is wrong in Camel it throws StackOverFlow error.
        // message.removeAttachment(fileName);
        message.addAttachment(newFileName, image);
        newFileName = _echoService.echoImage(newFileName);
        return newFileName;
    }

}
