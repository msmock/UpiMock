package com.smalljetty.controller;

import ch.ech.xmlns.ech_0213._1.Request;
import ch.ech.xmlns.ech_0213_commons._1.PersonFromUPIType;
import ch.ech.xmlns.ech_0213_commons._1.PidsFromUPIType;
import com.smalljetty.model.UpiPerson;
import org.w3c.dom.Document;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

@Path("services/SpidManagementService/")
public class RequestHandler_SpidManagementService {

    //String envPefix="src/main/java/"; //for dev.
    String envPefix = "classes/"; //for maven build

    @POST
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    public Response parseRequestContent(String fullRequest) throws Exception {

        String requestId = fullRequest.substring(fullRequest.indexOf("MessageID>") + 10);
        requestId = requestId.substring(0, requestId.indexOf("<"));
        String requestInfo = fullRequest.substring(fullRequest.indexOf(":request") - 5, fullRequest.indexOf(":request>") + 9);

        JAXBContext jaxbContext = JAXBContext.newInstance(Request.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();

        Request request = (Request) um.unmarshal(new StringReader(requestInfo));

        if (request.getContent().getActionOnSPID() != null) {
            System.out.println("TYPE2");
        } else {
            System.out.println("Type1");
        }

        String response = getResponseOfRequest2(request, requestId);

        //return generated response with sedex headers
        return Response.ok(response).header("Content-Type", "text/xml; charset=UTF-8").header("Connection", "Keep-Alive").build();
    }

    private String getResponseOfRequest2(Request request, String requestMessageId) throws Exception {

        //Create Response Object from XML File
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(envPefix + "com/smalljetty/app/SpidManagementService_Response1.xml"), StandardCharsets.UTF_8).forEach(line -> sb.append(line + System.getProperty("line.separator")));
        String responseStr = sb.toString();

        String responseHead = responseStr.substring(0, responseStr.indexOf(":response") - 5);
        responseHead = responseHead.replaceFirst("urn:uuid:a6cfdc10-ce29-4d06-9f51-35835a4a0b4d", requestMessageId);

        String responseContent = responseStr.substring(responseStr.indexOf(":response") - 5, responseStr.indexOf(":response>") + 10);

        String responseFooter = responseStr.substring(responseStr.indexOf(":response>") + 10);

        JAXBContext jaxbContext = JAXBContext.newInstance(ch.ech.xmlns.ech_0213._1.Response.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();
        ch.ech.xmlns.ech_0213._1.Response response = (ch.ech.xmlns.ech_0213._1.Response) um.unmarshal(new StringReader(responseContent));

        //generate response string
        String vnNumber = request.getContent().getPidsToUPI().get(0).getContent().get(0).getValue().toString();
        UpiPerson upiPerson = getUpiPerson(vnNumber);

        response.getHeader().setSenderId(request.getHeader().getSenderId());
        PidsFromUPIType pids = new PidsFromUPIType();
        pids.setVn(Long.getLong(vnNumber));
        pids.getSPID().clear();
        pids.getSPID().add(upiPerson.spid);
        response.getPositiveResponse().setPids(pids);
        response.getHeader().setReferenceMessageId(request.getHeader().getMessageId());
        response.getHeader().setMessageId(UUID.randomUUID().toString());

        response.getHeader().setMessageDate(getXmlGregorianCalendar(new Date()));
        response.getPositiveResponse().getPersonFromUPI().setFirstName(upiPerson.firstName);
        response.getPositiveResponse().getPersonFromUPI().setOfficialName(upiPerson.officialName);
        response.getPositiveResponse().getPersonFromUPI().setSex(upiPerson.sex);
        XMLGregorianCalendar bdayCal = getXmlGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd").parse(upiPerson.yearMonthDay));
        response.getPositiveResponse().getPersonFromUPI().getDateOfBirth().setYearMonthDay(bdayCal);
        response.getPositiveResponse().getPersonFromUPI().getNationalityData().getCountryInfo().get(0).getCountry().setCountryId(Integer.parseInt(upiPerson.countryId));
        response.getPositiveResponse().getPersonFromUPI().getNationalityData().getCountryInfo().get(0).getCountry().setCountryIdISO2(upiPerson.countryIdISO2);
        response.getPositiveResponse().getPersonFromUPI().getNationalityData().getCountryInfo().get(0).getCountry().setCountryNameShort(upiPerson.countryNameShort);

        //Marshal Object back to String
        StringWriter sw = new StringWriter();
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty("jaxb.fragment", Boolean.TRUE);
        m.marshal(response, sw);
        return responseHead + sw.toString() + responseFooter;
    }

    private XMLGregorianCalendar getXmlGregorianCalendar(Date date) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    }

    private UpiPerson getUpiPerson(String vn) throws Exception {

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(envPefix + "com/smalljetty/upiPersons/" + vn + ".xml");
        XPath xPath = XPathFactory.newInstance().newXPath();

        String firstName = xPath.evaluate("/upiPerson/firstName", document);
        String officialName = xPath.evaluate("/upiPerson/officialName", document);
        String sex = xPath.evaluate("/upiPerson/sex", document);
        String yearMonthDay = xPath.evaluate("/upiPerson/yearMonthDay", document);
        String countryId = xPath.evaluate("/upiPerson/countryId", document);
        String countryIdISO2 = xPath.evaluate("/upiPerson/countryIdISO2", document);
        String countryNameShort = xPath.evaluate("/upiPerson/countryNameShort", document);
        String spid = xPath.evaluate("/upiPerson/SPID", document);

        PersonFromUPIType person = new PersonFromUPIType();
        UpiPerson upiPerson = new UpiPerson(vn, firstName, officialName, sex, yearMonthDay, countryId,
                countryIdISO2, countryNameShort, spid);

        return upiPerson;
    }

}