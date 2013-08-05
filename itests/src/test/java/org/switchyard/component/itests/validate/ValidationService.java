package org.switchyard.component.itests.validate;

import java.io.InputStream;

import org.switchyard.annotations.OperationTypes;

public interface ValidationService {
    @OperationTypes(in="{urn:validate-test:reconsume-stream-xml:1.0}order",
            out="{urn:validate-test:reconsume-stream-xml:1.0}order")
    public InputStream testReconsumeStreamXml(InputStream input) throws Exception;
 }
