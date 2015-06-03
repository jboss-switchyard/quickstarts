package org.switchyard.transform.osgi.internal;

import java.net.URL;

import org.osgi.framework.Bundle;

/**
 * Implementation of TransformSource.
 */
public class TransformSourceImpl implements TransformSource {
    
    private Bundle _bundle;
    
    /**
     * Create a new TransformSourceImpl.
     * @param bundle a bundle containing a transform definition
     */
    public TransformSourceImpl(Bundle bundle) {
        _bundle = bundle;
    }
    
    @Override
    public URL getTransformsURL() {
        return _bundle.getEntry(TRANSFORMS_XML);
    }

    
}
