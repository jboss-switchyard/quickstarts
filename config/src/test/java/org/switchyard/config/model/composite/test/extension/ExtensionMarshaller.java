package org.switchyard.config.model.composite.test.extension;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;

public class ExtensionMarshaller extends BaseMarshaller {

    protected ExtensionMarshaller(Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(Configuration config) {
        if ("binding.extension".equals(config.getName())) {
            return new ExtensionBindingModel(config, getDescriptor());
        }
        return null;
    }

}
