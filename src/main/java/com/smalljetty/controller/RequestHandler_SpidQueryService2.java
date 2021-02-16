package com.smalljetty.controller;

import com.smalljetty.model.Config;
import com.smalljetty.model.RequestInfo;
import com.smalljetty.model.RequestType;
import com.smalljetty.model.UpiPerson;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Path("services/SpidQueryService2/")
public class RequestHandler_SpidQueryService2 {

    @POST
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    public Response parseRequestContent(String requestBody) throws Exception {

        String responseString;
        RequestInfo requestInfo = getRequestInfo(requestBody);

        if (requestInfo.requestType == RequestType.REQUEST_TYPE1)
            responseString = getType1Response(requestInfo);
         else
            responseString = getType2Response(requestInfo);

        responseString = "<?xml version='1.0' encoding='UTF-8'?>" + responseString;
        return Response.ok(responseString).header("Content-Type", "text/xml; charset=UTF-8").header("Connection", "Keep-Alive").build();
    }


    private RequestInfo getRequestInfo(String requestBody) throws Exception {

        InputSource source = new InputSource(new StringReader(requestBody));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        String vn = xPath.evaluate("/Envelope/Body/request/content/getInfoPersonRequest/pid/vn", document);
        String spid = xPath.evaluate("/Envelope/Body/request/content/getInfoPersonRequest/pid/SPID", document);

        if (vn.isEmpty())
            vn = getVnFromSpid(spid);

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.vn = vn;
        requestInfo.spid = spid;
        requestInfo.senderId = xPath.evaluate("/Envelope/Body/request/header/senderId", document);
        requestInfo.messageDate = xPath.evaluate("/Envelope/Body/request/header/messageDate", document);
        requestInfo.referenceMessageId = xPath.evaluate("/Envelope/Body/request/header/messageId", document);
        requestInfo.soapMessageId = xPath.evaluate("/Envelope/Header/MessageID", document);
        requestInfo.requestType = getRequestType(document);

        return requestInfo;
    }

    /**
     * @param document the request body
     * @return REQUEST_TYPE2 if the request is with the SPID, or REQUEST_TYPE1 if the request is with the VN
     */
    private RequestType getRequestType(Document document) throws Exception {

        String expression = "/Envelope/Body/request/content/getInfoPersonRequest/pid/SPID";
        Object result = XPathFactory.newInstance().newXPath().compile(expression).evaluate(document, XPathConstants.NODE);

        return (result == null) ? RequestType.REQUEST_TYPE1 : RequestType.REQUEST_TYPE2;
    }

    /**
     * resolve the vn to the person data and the SPID by file lookup.
     *
     * @param vn
     * @return the upi person match
     */
    private UpiPerson getUpiPerson(String vn) throws ParserConfigurationException, IOException, SAXException,
            XPathExpressionException {

        String uri = Config.envPefix + "com/smalljetty/upiPersons/" + vn + ".xml";
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri);

        XPath xPath = XPathFactory.newInstance().newXPath();

        UpiPerson upiPerson = new UpiPerson();

        upiPerson.vn = vn;
        upiPerson.spid = xPath.evaluate("/upiPerson/SPID", document);

        // person data
        upiPerson.firstName = xPath.evaluate("/upiPerson/firstName", document);
        upiPerson.officialName = xPath.evaluate("/upiPerson/officialName", document);
        upiPerson.sex = xPath.evaluate("/upiPerson/sex", document);
        upiPerson.birthDate = xPath.evaluate("/upiPerson/yearMonthDay", document);

        // birth place data
        upiPerson.municipalityId = xPath.evaluate("/upiPerson/municipalityId", document);
        upiPerson.municipalityName = xPath.evaluate("/upiPerson/municipalityName", document);
        upiPerson.cantonAbbreviation = xPath.evaluate("/upiPerson/cantonAbbreviation", document);
        upiPerson.historyMunicipalityId = xPath.evaluate("/upiPerson/historyMunicipalityId", document);

        // mothers name
        upiPerson.mothersFirstName= xPath.evaluate("/upiPerson/mothersFirstName", document);
        upiPerson.mothersOfficialName = xPath.evaluate("/upiPerson/mothersOfficialName", document);

        // fathers name
        upiPerson.fathersFirstName = xPath.evaluate("/upiPerson/fathersFirstName", document);
        upiPerson.fathersOfficialName = xPath.evaluate("/upiPerson/fathersOfficialName", document);

        // nationalityData
        upiPerson.countryId = xPath.evaluate("/upiPerson/countryId", document);
        upiPerson.countryIdISO2 = xPath.evaluate("/upiPerson/countryIdISO2", document);
        upiPerson.countryNameShort = xPath.evaluate("/upiPerson/countryNameShort", document);

        return upiPerson;
    }

    /**
     * REQUEST_TYPE1 if the request is with the VN
     */
    private String getType1Response(RequestInfo requestInfo) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = db.parse(Config.envPefix + "/com/smalljetty/app/SpidQuery_response-type-1.xml");

        //generate response string
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        String response = writer.getBuffer().toString().replaceAll("\n|\r", "");

        UpiPerson upiPerson = getUpiPerson(requestInfo.vn);

        return addResponseData(response, requestInfo, upiPerson);
    }

    /**
     * REQUEST_TYPE2 if the request is with the SPID
     */
    private String getType2Response(RequestInfo requestInfo) throws Exception {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = db.parse(Config.envPefix + "com/smalljetty/app/SpidQuery_response-type-2.xml");

        //generate response string
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        String response = writer.getBuffer().toString().replaceAll("\n|\r", "");

        requestInfo.vn = getVnFromSpid(requestInfo.spid);
        UpiPerson upiPerson = getUpiPerson(requestInfo.vn);

        return addResponseData(response, requestInfo, upiPerson);
    }

    /**
     * Search the UPI Persons files to resolve the SPID to the VN, if the request is with the SPID only.
     *
     * @param spid
     * @return VN as String
     */
    private String getVnFromSpid(String spid) throws Exception {

        File dir = new File(Config.envPefix + "com/smalljetty/upiPersons");
        File[] files = dir.listFiles();

        if (files == null)
            throw new Exception("No files found in dir " + dir.getAbsolutePath());

        for (File xmlFile : files) {

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(xmlFile.getAbsolutePath());

            XPath xPath = XPathFactory.newInstance().newXPath();

            String document_spid = xPath.evaluate("/upiPerson/SPID", document);
            if (document_spid.equals(spid))
                return xPath.evaluate("/upiPerson/vn", document);
        }

        throw new Exception("Could not find person with spid "+spid+ " in test data!");
    }

    private String addResponseData(String response, RequestInfo requestInfo, UpiPerson upiPerson){

        response = response.replaceAll("\\$recipientId", requestInfo.senderId);
        response = response.replaceAll("\\$messageId", UUID.randomUUID().toString());
        response = response.replaceAll("\\$referenceMessageId", requestInfo.referenceMessageId);

        response = response.replaceAll("\\$echoSpid", requestInfo.spid);
        response = response.replaceAll("\\$echoVn", requestInfo.vn);

        response = response.replaceAll("\\$upiVn", upiPerson.vn);
        response = response.replaceAll("\\$upiSpid", upiPerson.spid);

        // person data
        response = response.replaceAll("\\$firstName", upiPerson.firstName);
        response = response.replaceAll("\\$officialName", upiPerson.officialName);
        response = response.replaceAll("\\$sex", upiPerson.sex);
        response = response.replaceAll("\\$dateOfBirth", upiPerson.birthDate);

        // birth place data
        response = response.replaceAll("\\$municipalityId", upiPerson.municipalityId);
        response = response.replaceAll("\\$municipalityName", upiPerson.municipalityName);
        response = response.replaceAll("\\$cantonAbbreviation", upiPerson.cantonAbbreviation);
        response = response.replaceAll("\\$historyMunicipalityId", upiPerson.historyMunicipalityId);

        // mothers name
        response = response.replaceAll("\\$mothersFirstName", upiPerson.mothersFirstName);
        response = response.replaceAll("\\$mothersOfficialName", upiPerson.mothersOfficialName);

        // fathers name
        response = response.replaceAll("\\$fathersFirstName", upiPerson.fathersFirstName);
        response = response.replaceAll("\\$fathersOfficialName", upiPerson.fathersOfficialName);

        // nationality data
        response = response.replaceAll("\\$countryId", upiPerson.countryId);
        response = response.replaceAll("\\$countryCode", upiPerson.countryIdISO2);
        response = response.replaceAll("\\$countryName", upiPerson.countryNameShort);

        return response;
    }



}