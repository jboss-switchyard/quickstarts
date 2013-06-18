package org.switchyard.internal.transform;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.switchyard.transform.TransformResolver;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * Base implementation of the TransformResolver strategy that facilitates resolving of 
 * direct/indirect transform sequences.
 */
public class BaseTransformResolver implements TransformResolver {
    private TransformerRegistry _registry;

    /**
     * Create a new TransformResolver instance.
     */    
    public BaseTransformResolver() {
        super();
    }


    /**
     * Create a new TransformResolver instance and associate it with a TransformRegistry.
     * @param registry set of transformers to add to registry
     */
    public BaseTransformResolver(TransformerRegistry registry) {
        super();
        this._registry = registry;
    }


    @Override
    public TransformSequence resolveSequence(QName from, QName to) {
        ArrayList<Transformer<?,?>> fromMatches = new ArrayList<Transformer<?,?>>();
        ArrayList<Transformer<?,?>> toMatches = new ArrayList<Transformer<?,?>>();
        TransformSequence transformSequence = null;
        
        for (Transformer<?,?> entry : _registry.getRegisteredTransformers()) {
            if ((entry.getFrom().equals(from)) && (entry.getTo().equals(to))) {
                transformSequence = TransformSequence.from(entry.getFrom()).to(entry.getTo());
                break;
            } else if (entry.getFrom().equals(from)) {
                fromMatches.add(entry);
            } else if (entry.getTo().equals(to)) {
                toMatches.add(entry);
            }            
        }

        if (transformSequence == null) {
            // match 1st occurence of the first level of indirection (A->B,B->C instead of A->C)
            for (Transformer<?,?> fromTransformer : fromMatches) {
                for (Transformer<?,?> toTransformer : toMatches) {
                    if (fromTransformer.getTo().equals(toTransformer.getFrom())) {
                        transformSequence = TransformSequence.from(fromTransformer.getFrom()).to(fromTransformer.getTo()).to(to);
                        break;
                    }
                }
            }
        }
        
        return transformSequence;
    }

    /**
     * Get the associated TransformerRegistry.
     * @return TransformerRegistry.
     */
    public TransformerRegistry getRegistry() {
        return _registry;
    }

    /**
     * Sets the TransformerRegistry instance.
     * @param registry transformer registry
     */
    public void setRegistry(TransformerRegistry registry) {
        this._registry = registry;
    }

    
}
