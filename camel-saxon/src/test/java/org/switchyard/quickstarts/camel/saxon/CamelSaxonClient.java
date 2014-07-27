package org.switchyard.quickstarts.camel.saxon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

public final class CamelSaxonClient {

    private static final String request_prefix = 
"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
"   <soapenv:Header/>" +
"   <soapenv:Body>" +
"      <greet xmlns=\"urn:switchyard-quickstart:camel-saxon:0.1.0\">";

    private static final String request_suffix = "</greet>" +
"   </soapenv:Body>" +
"</soapenv:Envelope>";

    public static void main(String[] args) {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your name: ");

            String name = reader.readLine();
            String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
            soapMixIn.postString("http://localhost:" + port + "/quickstart-camel-saxon/GreetingService", request_prefix + name + request_suffix);
            System.out.println("See the server console for output");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            soapMixIn.uninitialize();
        }
    }

}
