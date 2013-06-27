/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.quickstarts.camel.soap.proxy;

import static javax.jws.soap.SOAPBinding.Style.DOCUMENT;
import static javax.jws.soap.SOAPBinding.Use.LITERAL;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@WebService(name="ReverseService", serviceName="ReverseService", targetNamespace="urn:switchyard-quickstart:camel-soap-proxy:1.0")
@SOAPBinding(style=DOCUMENT, use=LITERAL)
public class ReverseService {

    @WebMethod(action="urn:switchyard-quickstart:camel-soap-proxy:1.0")
    @WebResult(name="text")
    public String reverse(@WebParam(name="text") String text) {
        return new StringBuilder(text).reverse().toString();
    }

    @WebMethod(action="urn:switchyard-quickstart:camel-soap-proxy:1.0")
    @WebResult(name="text")
    public String upper(@WebParam(name="text") String text) {
        return text.toUpperCase();
    }

}
