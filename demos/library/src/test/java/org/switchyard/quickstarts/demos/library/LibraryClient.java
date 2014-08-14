/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.quickstarts.demos.library;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.quickstarts.demos.library.types.Book;
import org.switchyard.quickstarts.demos.library.types.Loan;
import org.switchyard.quickstarts.demos.library.types.LoanRequest;
import org.switchyard.quickstarts.demos.library.types.LoanResponse;
import org.switchyard.quickstarts.demos.library.types.ReturnRequest;
import org.switchyard.quickstarts.demos.library.types.ReturnResponse;
import org.switchyard.quickstarts.demos.library.types.Suggestion;
import org.switchyard.quickstarts.demos.library.types.SuggestionRequest;
import org.switchyard.quickstarts.demos.library.types.SuggestionResponse;

public final class LibraryClient {

    private static final String SOAP_REQUEST_PREFIX = "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'><SOAP-ENV:Header>PID</SOAP-ENV:Header><SOAP-ENV:Body>";
    private static final String SOAP_REQUEST_SUFFIX = "</SOAP-ENV:Body></SOAP-ENV:Envelope>";

    private static final QName _SuggestionRequest_QNAME = new QName("urn:switchyard-quickstart-demo:library:1.0", "suggestionRequest");
    private static final QName _LoanRequest_QNAME = new QName("urn:switchyard-quickstart-demo:library:1.0", "loanRequest");
    private static final QName _ReturnRequest_QNAME = new QName("urn:switchyard-quickstart-demo:library:1.0", "returnRequest");

    private final HTTPMixIn _httpMixIn;
    private final String _port;

    public LibraryClient(HTTPMixIn httpMixIn, String port) {
        _httpMixIn = httpMixIn;
        _port = port;
    }

    public void testLibraryServices() throws Exception {
        System.out.println();
        // get 1st suggestion
        Suggestion suggestion1_Zombie = getSuggestion("Zombie");
        Book book1_WorldWarZ = suggestion1_Zombie.getBook();
        System.out.println("Received suggestion for book: " + book1_WorldWarZ.getTitle() + " (isbn: " + book1_WorldWarZ.getIsbn() + ")");
        Assert.assertEquals("World War Z", book1_WorldWarZ.getTitle());
        // take out 1st loan
        System.out.println("Attempting 1st loan for isbn: " + book1_WorldWarZ.getIsbn());
        Loan loan1_WorldWarZ = attemptLoan(book1_WorldWarZ.getIsbn());
        System.out.println("1st loan approved? " + loan1_WorldWarZ.isApproved());
        Assert.assertTrue(loan1_WorldWarZ.isApproved());
        // 2nd loan should not be approved since 1st loan hasn't been returned
        System.out.println("Attempting 2nd loan for isbn: " + book1_WorldWarZ.getIsbn());
        Loan loan2_WorldWarZ = attemptLoan(book1_WorldWarZ.getIsbn());
        System.out.println("2nd loan approved? " + loan2_WorldWarZ.isApproved());
        Assert.assertFalse(loan2_WorldWarZ.isApproved());
        // return 1st loan
        System.out.println("Returning 1st loan for isbn: " + loan1_WorldWarZ.getBook().getIsbn());
        boolean return1_ack = returnLoan(loan1_WorldWarZ);
        System.out.println("1st loan return acknowledged? " + return1_ack);
        Assert.assertTrue(return1_ack);
        // try 2nd loan again; this time it should work
        System.out.println("Re-attempting 2nd loan for isbn: " + book1_WorldWarZ.getIsbn());
        loan2_WorldWarZ = attemptLoan(book1_WorldWarZ.getIsbn());
        System.out.println("Re-attempt of 2nd loan approved? " + loan2_WorldWarZ.isApproved());
        Assert.assertTrue(loan2_WorldWarZ.isApproved());
        // get 2nd suggestion, and since 1st book not available (again), 2nd match will return
        Suggestion suggestion2_TheZombieSurvivalGuide = getSuggestion("Zombie");
        Book book2_TheZombieSurvivalGuide = suggestion2_TheZombieSurvivalGuide.getBook();
        System.out.println("Received suggestion for book: " + book2_TheZombieSurvivalGuide.getTitle() + " (isbn: " + book2_TheZombieSurvivalGuide.getIsbn() + ")");
        Assert.assertEquals("The Zombie Survival Guide", book2_TheZombieSurvivalGuide.getTitle());
        // take out 3rd loan
        System.out.println("Attempting 3rd loan for isbn: " + book2_TheZombieSurvivalGuide.getIsbn());
        Loan loan3_TheZombieSurvivalGuide = attemptLoan(book2_TheZombieSurvivalGuide.getIsbn());
        System.out.println("3rd loan approved? " + loan3_TheZombieSurvivalGuide.isApproved());
        Assert.assertTrue(loan3_TheZombieSurvivalGuide.isApproved());
        // return 2nd loan
        System.out.println("Returning 2nd loan for isbn: " + loan2_WorldWarZ.getBook().getIsbn());
        boolean return2_ack = returnLoan(loan2_WorldWarZ);
        System.out.println("2nd loan return acknowledged? " + return2_ack);
        Assert.assertTrue(return2_ack);
        // return 3rd loan
        System.out.println("Returning 3rd loan for isbn: " + loan3_TheZombieSurvivalGuide.getBook().getIsbn());
        boolean return3_ack = returnLoan(loan3_TheZombieSurvivalGuide);
        System.out.println("3rd loan return acknowledged? " + return3_ack);
        Assert.assertTrue(return3_ack);
        System.out.println();
    }

    private Suggestion getSuggestion(String keyword) throws Exception {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setKeyword(keyword);
        String soapRequest = wrapRequest(_SuggestionRequest_QNAME, SuggestionRequest.class, suggestionRequest, null);
        String soapResponse = _httpMixIn.postString("http://localhost:" + _port + "/suggestion/SuggestionService", soapRequest);
        SuggestionResponse suggestionResponse = unwrapResponse(SuggestionResponse.class, soapResponse);
        return suggestionResponse.getSuggestion();
    }

    private Loan attemptLoan(String isbn) throws Exception {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setIsbn(isbn);
        String soapRequest = wrapRequest(_LoanRequest_QNAME, LoanRequest.class, loanRequest, null);
        String soapResponse = _httpMixIn.postString("http://localhost:" + _port + "/loan/LoanService", soapRequest);
        LoanResponse loanResponse = unwrapResponse(LoanResponse.class, soapResponse);
        return loanResponse.getLoan();
    }

    private boolean returnLoan(Loan loan) throws Exception {
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setLoan(loan);
        String soapRequest = wrapRequest(_ReturnRequest_QNAME, ReturnRequest.class, returnRequest, loan.getId());
        String soapResponse = _httpMixIn.postString("http://localhost:" + _port + "/loan/LoanService", soapRequest);
        ReturnResponse returnResponse = unwrapResponse(ReturnResponse.class, soapResponse);
        return returnResponse.isAcknowledged();
    }

    private <T> String wrapRequest(QName name, Class<T> declaredType, T value, String pid) throws Exception {
        JAXBElement<T> e = new JAXBElement<T>(name, declaredType, null, value);
        JAXBContext ctx = JAXBContext.newInstance("org.switchyard.quickstarts.demos.library.types");
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        String processInstanceId = pid != null ? "<bpm:processInstanceId xmlns:bpm='urn:switchyard-component-bpm:bpm:1.0'>" + pid + "</bpm:processInstanceId>" : "";
        sw.write(SOAP_REQUEST_PREFIX.replaceFirst("PID", processInstanceId));
        m.marshal(e, sw);
        sw.write(SOAP_REQUEST_SUFFIX);
        return sw.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> T unwrapResponse(Class<T> declaredType, String envelope) throws Exception {
        // TODO: replace with generic DOM code vs. using config helper code
        Configuration body = new ConfigurationPuller().pull(new StringReader(envelope)).getFirstChild("Body").getChildren().iterator().next();
        String content = body.toString();
        if (body.getName().equals("Fault")) {
            throw new Exception("Fault returned: " + content);
        } else {
            JAXBContext ctx = JAXBContext.newInstance("org.switchyard.quickstarts.demos.library.types");
            Unmarshaller u = ctx.createUnmarshaller();
            Object o = u.unmarshal(new StringReader(content));
            if (o instanceof JAXBElement) {
                o = ((JAXBElement<T>)o).getValue();
            }
            return declaredType.cast(o);
        }
    }

    public static void main(String... args) throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
            LibraryClient client = new LibraryClient(httpMixIn, port);
            client.testLibraryServices();
        } finally {
            httpMixIn.uninitialize();
        }
    }

}
