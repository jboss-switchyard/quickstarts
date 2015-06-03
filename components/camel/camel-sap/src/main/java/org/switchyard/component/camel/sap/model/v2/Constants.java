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
package org.switchyard.component.camel.sap.model.v2;

import org.switchyard.config.model.composite.BindingModel;

/**
 * V2 camel-sap constants.
 */
public final class Constants {

    private Constants() {
    }

    /** sap. */
    public static final String SAP = "sap";
    /** binding.sap. */
    public static final String BINDING_SAP = BindingModel.BINDING + "." + SAP;

    /** idoclist-server. */
    public static final String IDOCLIST_SERVER = "idoclist-server";
    /** srfc-server. */
    public static final String SRFC_SERVER = "srfc-server";
    /** trfc-server. */
    public static final String TRFC_SERVER = "trfc-server";
    /** idoc-destination. */
    public static final String IDOC_DESTINATION = "idoc-destination";
    /** idoclist-destination. */
    public static final String IDOCLIST_DESTINATION = "idoclist-destination";
    /** qidoc-destination. */
    public static final String QIDOC_DESTINATION = "qidoc-destination";
    /** qidoclist-destination. */
    public static final String QIDOCLIST_DESTINATION = "qidoclist-destination";
    /** qrfc-destination. */
    public static final String QRFC_DESTINATION = "qrfc-destination";
    /** srfc-destination. */
    public static final String SRFC_DESTINATION = "srfc-destination";
    /** trfc-destination. */
    public static final String TRFC_DESTINATION = "trfc-destination";

    /** serverName. */
    public static final String SERVER_NAME = "serverName";
    /** destinationName. */
    public static final String DESTINATION_NAME = "destinationName";
    /** rfcName. */
    public static final String RFC_NAME = "rfcName";
    /** queueName. */
    public static final String QUEUE_NAME = "queueName";
    /** idocType. */
    public static final String IDOC_TYPE = "idocType";
    /** idocTypeExtension. */
    public static final String IDOC_TYPE_EXTENSION = "idocTypeExtension";
    /** systemRelease. */
    public static final String SYSTEM_RELEASE = "systemRelease";
    /** applicationRelease. */
    public static final String APPLICATION_RELEASE = "applicationRelease";
    /** transacted. */
    public static final String TRANSACTED = "transacted";
}
