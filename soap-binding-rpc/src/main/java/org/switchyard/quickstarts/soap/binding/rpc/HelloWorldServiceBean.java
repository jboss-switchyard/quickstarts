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
package org.switchyard.quickstarts.soap.binding.rpc;

import org.switchyard.component.bean.Service;

/**
 * HelloWorldService Implementation.
 */
@Service(HelloWorldService.class)
public class HelloWorldServiceBean implements HelloWorldService {

    @Override
    public String sayHello(SayHelloExternal input) {
        // TODO: Currently not possible to set property on return path for CDI Beans
        /*if (input.equals("500")) {
            context.setProperty(SOAPContextMapper.HTTP_RESPONSE_STATUS).addLabels(new String[]{EndpointLabel.HTTP.label()});
        }*/
        String greeting = "Hello World Greeting for '" + input.getToWhom() + "' in " + input.getLanguage() + " on a " + input.getDay() + "!";
        return greeting;
    }

}
