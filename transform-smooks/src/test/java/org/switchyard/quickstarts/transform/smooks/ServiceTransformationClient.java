package org.switchyard.quickstarts.transform.smooks;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class ServiceTransformationClient {

    private static final String URL = "http://localhost:8080/quickstart-transform-xslt/OrderService";
    private static final String XML = "src/test/resources/xml/soap-request.xml";

    /**
     * Private no-args constructor.
     */
    private ServiceTransformationClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] ignored) throws Exception {

        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String result = soapMixIn.postFile(URL, XML);
            System.out.println("SOAP Reply:\n" + result);
        } finally {
            soapMixIn.uninitialize();
        }
    }
}
