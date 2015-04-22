package org.switchyard.component.clojure.config.model.v1;

import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * Version 1 marshaller for a Clojure model.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1ClojureModelMarshaller extends BaseMarshaller {

    /**
     * Sole constructor.
     * 
     * @param desc The descriptor for this model.
     */
    public V1ClojureModelMarshaller(Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(final Configuration config) {
        final String name = config.getName();
        if (name.startsWith(ComponentImplementationModel.IMPLEMENTATION)) {
            return new V1ClojureComponentImplementationModel(config, getDescriptor());
        }
        
        if (name.startsWith(ClojureComponentImplementationModel.SCRIPT)) {
            return new V1ClojureScriptModel(config, getDescriptor());
        }
        
        return null;
    }

}
