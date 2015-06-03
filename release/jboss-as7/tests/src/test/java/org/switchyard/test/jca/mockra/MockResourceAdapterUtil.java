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
package org.switchyard.test.jca.mockra;

import javax.resource.cci.MessageListener;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.switchyard.common.type.Classes;

public class MockResourceAdapterUtil {
    public static final String IRONJACAMAR_XML = "org/switchyard/test/jca/cci-test-ironjacamar.xml";
    public static final String RESOURCE_ADAPTER_XML = "org/switchyard/test/jca/cci-test-mock-ra.xml";
    public static final String ADAPTER_NAME = "myeis-ra";
    public static final String ADAPTER_ARCHIVE_NAME = ADAPTER_NAME + ".rar";
    public static final String LIB_JAR_NAME = "lib.jar";
    public static final String JNDI_CONNECTION_FACTORY = "java:jboss/MyEISConnectionFactory";
    public static final String JNDI_ADAPTER = "MyEISResourceAdapter";
    public static final String MCF_CLASS = MockManagedConnectionFactory.class.getName();

    public static ResourceAdapterArchive createMockResourceAdapterArchive() {
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class, LIB_JAR_NAME);
        ja.addClasses(MessageListener.class, MockActivationSpec.class, MockConnection.class,
                MockConnectionFactory.class, MockConnectionManager.class,
                MockManagedConnection.class, MockManagedConnectionFactory.class,
                MockResourceAdapter.class, MockXAResource.class, InteractionListener.class,
                MockInteraction.class, MockRecordFactory.class, MockMappedRecord.class, MockIndexedRecord.class);
        try {
            return ShrinkWrap.create(ResourceAdapterArchive.class, ADAPTER_ARCHIVE_NAME)
                             .addAsLibrary(ja)
                             .setResourceAdapterXML(Classes.getResource(RESOURCE_ADAPTER_XML))
                             .addAsManifestResource(IRONJACAMAR_XML, "ironjacamar.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
