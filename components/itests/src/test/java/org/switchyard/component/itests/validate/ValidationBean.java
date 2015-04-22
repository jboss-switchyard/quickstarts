package org.switchyard.component.itests.validate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.switchyard.component.bean.Service;

@Service(ValidationService.class)
public class ValidationBean implements ValidationService {

    @Override
    public InputStream testReconsumeStreamXml(InputStream input) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
        }
        return new ByteArrayInputStream(buf.toString().getBytes());
    }
}
