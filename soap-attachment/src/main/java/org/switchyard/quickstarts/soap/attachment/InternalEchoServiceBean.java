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
package org.switchyard.quickstarts.soap.attachment;

import javax.activation.DataSource;
import javax.inject.Inject;

import org.switchyard.Message;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(InternalEchoService.class)
public class InternalEchoServiceBean implements InternalEchoService {

    @Inject
    private Message message;

    @Inject
    @Reference
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
