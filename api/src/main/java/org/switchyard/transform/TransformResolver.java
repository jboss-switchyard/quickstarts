package org.switchyard.transform;

import javax.xml.namespace.QName;

/**
 * Transform resolver operations for the Transformer Registry.
 */
public interface TransformResolver {
    
    /**
     * Resolve a transform sequence wiring transformers.
     * @param from from
     * @param to to
     * @return transformer
     */
    TransformSequence resolveSequence(QName from, QName to);
}
