package com.smalljetty.controller;

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

    private final String envPefix="target/classes/"; //for dev.
    // private final String envPefix = "classes/"; //for maven build

    //todo: refactor handling request1/request2
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    public Response parseRequestContent(String requestBody) throws Exception {

        String responseString;
        RequestInfo requestInfo = getRequestInfo(requestBody);

        if (requestInfo.getRequestType() == RequestType.REQUEST_TYPE1)
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

        String senderId = xPath.evaluate("/Envelope/Body/request/header/senderId", document);
        String messageDate = xPath.evaluate("/Envelope/Body/request/header/messageDate", document);
        String vn = xPath.evaluate("/Envelope/Body/request/content/getInfoPersonRequest/pid/vn", document);
        String spid = xPath.evaluate("/Envelope/Body/request/content/getInfoPersonRequest/pid/SPID", document);

        String messageId = xPath.evaluate("/Envelope/Body/request/header/messageId", document);
        String soapMessageId = xPath.evaluate("/Envelope/Header/MessageID", document);

        if (vn.isEmpty())
            vn = getVnFromSpid(spid);

        RequestType requestType = getRequestType(document);
        return new RequestInfo(senderId, vn, messageDate, messageId, soapMessageId, requestType, spid);

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
     * Search the UPI Persons files to resolve the SPID to the VN, if the request is with the SPID only.
     *
     * @param spid
     * @return VN as String
     */
    private String getVnFromSpid(String spid) throws Exception {

        File dir = new File(envPefix + "com/smalljetty/upiPersons");
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

        return "should never be reached ..";
    }

    /**
     * resolve the vn to the person data and the SPID by file lookup.
     *
     * @param vn
     * @return the upi person match
     */
    private UpiPerson getUpiPerson(String vn) throws ParserConfigurationException, IOException, SAXException,
            XPathExpressionException {

        String uri = envPefix + "com/smalljetty/upiPersons/" + vn + ".xml";
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri);

        XPath xPath = XPathFactory.newInstance().newXPath();

        String firstName = xPath.evaluate("/upiPerson/firstName", document);
        String officialName = xPath.evaluate("/upiPerson/officialName", document);
        String sex = xPath.evaluate("/upiPerson/sex", document);
        String yearMonthDay = xPath.evaluate("/upiPerson/yearMonthDay", document);
        String countryId = xPath.evaluate("/upiPerson/countryId", document);
        String countryIdISO2 = xPath.evaluate("/upiPerson/countryIdISO2", document);
        String countryNameShort = xPath.evaluate("/upiPerson/countryNameShort", document);
        String spid = xPath.evaluate("/upiPerson/SPID", document);

        return new UpiPerson(vn, firstName, officialName, sex, yearMonthDay, countryId,
                countryIdISO2, countryNameShort, spid);
    }

    /**
     * REQUEST_TYPE1 if the request is with the VN
     */
    private String getType1Response(RequestInfo requestInfo) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = db.parse(envPefix + "/com/smalljetty/app/SpidQueryService2_Response1.xml");

        //generate response string
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        String response = writer.getBuffer().toString().replaceAll("\n|\r", "");

        response = response.replaceAll("sedex://T4-130114-5", requestInfo.getSenderId());
        response = response.replaceAll("7560520268326", requestInfo.getVn());
        response = response.replaceAll("3fa45e307b7c4b2fa6180c21d21c7dcf", requestInfo.getReferenceMessageId());
        response = response.replaceAll("f89b611c-adde-44eb-8418-077c0b4b1939", UUID.randomUUID().toString());

        response = replaceXmlValue(response, "//Envelope/Body/response/header/messageDate/text()", OffsetDateTime.now().toString());
        response = replaceXmlValue(response, "//Envelope/Body/response/header/messageDate/text()", requestInfo.getMessageDate());
        response = replaceXmlValue(response, "//Envelope/Header/RelatesTo/text()", requestInfo.getSoapMessageId());

        UpiPerson upiPerson = getUpiPerson(requestInfo.getVn());
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/firstName/text()", upiPerson.firstName);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/officialName/text()", upiPerson.officialName);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/sex/text()", upiPerson.sex);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/dateOfBirth/yearMonthDay/text()", upiPerson.yearMonthDay);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/nationalityData/countryInfo/country/countryId/text()", upiPerson.countryId);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/nationalityData/countryInfo/country/countryIdISO2/text()", upiPerson.countryIdISO2);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/nationalityData/countryInfo/country/countryNameShort/text()", upiPerson.countryNameShort);

        return response;
    }

    /**
     * REQUEST_TYPE2 if the request is with the SPID
     */
    private String getType2Response(RequestInfo requestInfo) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = db.parse(envPefix + "com/smalljetty/app/SpidQueryService2_Response2.xml");

        //generate response string
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        String response = writer.getBuffer().toString().replaceAll("\n|\r", "");

        response = response.replaceAll("sedex://T4-321574-2", requestInfo.getSenderId());
        response = response.replaceAll("7560520333642", requestInfo.getVn());
        response = response.replaceAll("559f64d63aee4c4ead5e501901b5729f", requestInfo.getReferenceMessageId());
        response = response.replaceAll("399aeed2-6094-45db-aece-fa5256861fa8", UUID.randomUUID().toString());

        response = replaceXmlValue(response, "//Envelope/Body/response/header/messageDate/text()", OffsetDateTime.now().toString());
        response = replaceXmlValue(response, "//Envelope/Body/response/header/messageDate/text()", requestInfo.getMessageDate());
        response = replaceXmlValue(response, "//Envelope/Header/RelatesTo/text()", requestInfo.getSoapMessageId());

        UpiPerson upiPerson = getUpiPerson(requestInfo.getVn());

        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/firstName/text()", upiPerson.firstName);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/officialName/text()", upiPerson.officialName);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/sex/text()", upiPerson.sex);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/dateOfBirth/yearMonthDay/text()", upiPerson.yearMonthDay);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/nationalityData/countryInfo/country/countryId/text()", upiPerson.countryId);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/nationalityData/countryInfo/country/countryIdISO2/text()", upiPerson.countryIdISO2);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/personFromUPI/nationalityData/countryInfo/country/countryNameShort/text()", upiPerson.countryNameShort);
        response = replaceXmlValue(response, "//Envelope/Body/response/positiveResponse/getInfoPersonResponse/pids/SPID/text()", upiPerson.spid);

        return response;
    }


    private String replaceXmlValue(String xmlString, String expression, String textToInsert) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document documentOfString = db.parse(is);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        NodeList messageNode = (NodeList) xPath.compile(expression).evaluate(documentOfString, XPathConstants.NODESET);
        messageNode.item(0).setNodeValue(textToInsert);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(documentOfString), new StreamResult(writer));

        return writer.getBuffer().toString();
    }


}