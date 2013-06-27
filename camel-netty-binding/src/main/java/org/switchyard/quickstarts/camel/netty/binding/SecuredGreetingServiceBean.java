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
package org.switchyard.quickstarts.camel.netty.binding;

import static org.switchyard.policy.SecurityPolicy.AUTHORIZATION;
import static org.switchyard.policy.SecurityPolicy.CLIENT_AUTHENTICATION;
import static org.switchyard.policy.SecurityPolicy.CONFIDENTIALITY;

import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Service;

/**
 * A POJO Service implementation.
 *
 * @author Lukasz Dywicki
 */
@Requires(security = {CONFIDENTIALITY, CLIENT_AUTHENTICATION, AUTHORIZATION})
@Service(name ="SecuredGreetingService", componentName ="SecuredGreetingService", value=GreetingService.class)
public class SecuredGreetingServiceBean extends GreetingServiceBean {

    /**
     * Creates secured version of greeting service.
     */
    public SecuredGreetingServiceBean() {
        super("Secured");
    }

}
