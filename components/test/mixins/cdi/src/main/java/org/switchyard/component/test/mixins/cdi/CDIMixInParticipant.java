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
package org.switchyard.component.test.mixins.cdi;

import org.jboss.weld.bootstrap.spi.Deployment;

/**
 * Interface for MixIns which are interested in populating Weld deployment during
 * startup of CDI MixIn.
 */
public interface CDIMixInParticipant {

    /**
     * Method called after Weld SE deployment is created.
     * 
     * @param deployment Weld deployment instance.
     * @throws Exception If something goes wrong and CDI environment can not be
     * populated with necessary beans.
     */
    void participate(Deployment deployment) throws Exception;


}
