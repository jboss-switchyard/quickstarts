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
package org.switchyard.component.bean.inject;

import java.io.IOException;

import javax.activation.DataSource;
import javax.inject.Inject;

import org.switchyard.Context;
import org.switchyard.Message;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.bean.Service;

@Service(InjectService.class)
public class InjectServiceImpl implements InjectService {

    @Inject
    private Context context;

    @Inject
    private Message message;

    @Override
    public String doSomething(String in) {
        String propC = (String)context.getProperty("someProp").getValue();
        String propM = (String)message.getContext().getProperty("someProp").getValue();
        boolean propertyMatch = propC.equals(propM);
        boolean contentMatch = in.equals(message.getContent(String.class));
        DataSource attach = message.getAttachment("someAttach");
        String attachData;
        try {
            attachData = new StringPuller().pull(attach.getInputStream());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        boolean attachMatch = "someAttach".equals(attach.getName()) && "text/plain".equals(attach.getContentType()) && "someAttachData".equals(attachData);
        return propertyMatch + ", " + contentMatch + ", " + attachMatch;
    }

}
