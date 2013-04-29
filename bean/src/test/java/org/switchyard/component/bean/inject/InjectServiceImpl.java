/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
