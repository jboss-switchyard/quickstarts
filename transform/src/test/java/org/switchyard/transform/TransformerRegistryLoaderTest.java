package org.switchyard.transform;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.internal.transform.BaseTransformerRegistry;

public class TransformerRegistryLoaderTest {

    private static final String MISSING_TRANSFORM_XML = 
            "/org/switchyard/transform/MissingTransformerTest.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void transformerClassNotFound() throws Exception {
        SwitchYardModel switchyard = _puller.pull(MISSING_TRANSFORM_XML, getClass());
        TransformerRegistryLoader loader = new TransformerRegistryLoader(new BaseTransformerRegistry());
        try {
            loader.newTransformers(switchyard.getTransforms().getTransforms().get(0));
            // the above should have resulted in an exception
            Assert.fail("missing transformer class should result in SwitchYardException");
        } catch (SwitchYardException syEx) {
            // expected outcome
            return;
        }
    }
}
