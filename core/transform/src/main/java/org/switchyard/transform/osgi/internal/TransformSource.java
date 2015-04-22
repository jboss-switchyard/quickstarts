package org.switchyard.transform.osgi.internal;

import java.net.URL;

/**
 *  Service registered by bundles which contain transformer definitions.  Used primarily
 *  by SwitchYard runtime components to indicate they have OOTB transformers that need 
 *  to be registered by the SwitchYard container.
 */
public interface TransformSource {

    /**
     * Default location of transformer definition.
     */
    String TRANSFORMS_XML = "META-INF/switchyard/transforms.xml";

    /**
     * URL pointing to the absolute location of a transformer definition.
     * @return transformer descriptor URL
     */
    URL getTransformsURL();
}
