package util;

import com.smalljetty.model.UpiPerson;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class ListPersons {

    private static String envPefix = "src/main/java/"; //for dev.
    //private static String envPefix="classes/"; //for maven build

    public static void main(String[] args) throws Exception {

        File dir = new File(envPefix + "com/smalljetty/upiPersons");
        File[] directoryListing = dir.listFiles();

        if (directoryListing == null)
            throw new Exception("No files found in dir " + dir.getAbsolutePath());

        String header = "vn" + ", spid" + ", firstName" + ", officialName" + ", birthDate";

        System.out.println(header);

        for (File xmlFile : directoryListing) {

            try {

                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile.getAbsolutePath());
                UpiPerson upiPerson = getUpiPerson(document);
                System.out.println(upiPerson);

            } catch (Exception e) {
                System.out.println("Error while parsing " + xmlFile);
            }
        }
    }

    private static UpiPerson getUpiPerson(Document document) throws Exception {

        XPath xPath = XPathFactory.newInstance().newXPath();

        UpiPerson upiPerson = new UpiPerson();

        upiPerson.vn = xPath.evaluate("/upiPerson/vn", document);
        upiPerson.spid = xPath.evaluate("/upiPerson/SPID", document);

        // person data
        upiPerson.firstName = xPath.evaluate("/upiPerson/firstName", document);
        upiPerson.officialName = xPath.evaluate("/upiPerson/officialName", document);
        upiPerson.sex = xPath.evaluate("/upiPerson/sex", document);
        upiPerson.birthDate = xPath.evaluate("/upiPerson/yearMonthDay", document);

        // birth place data
        upiPerson.setMunicipalityId(xPath.evaluate("/upiPerson/municipalityId", document));
        upiPerson.setMunicipalityName(xPath.evaluate("/upiPerson/municipalityName", document));
        upiPerson.setCantonAbbreviation(xPath.evaluate("/upiPerson/cantonAbbreviation", document));
        upiPerson.setHistoryMunicipalityId(xPath.evaluate("/upiPerson/historyMunicipalityId", document));

        // mothers name
        upiPerson.setMothersFirstName(xPath.evaluate("/upiPerson/mothersFirstName", document));
        upiPerson.setMothersOfficialName(xPath.evaluate("/upiPerson/mothersOfficialName", document));

        // fathers name
        upiPerson.setFathersFirstName(xPath.evaluate("/upiPerson/fathersFirstName", document));
        upiPerson.setFathersOfficialName(xPath.evaluate("/upiPerson/fathersOfficialName", document));

        // nationalityData
        upiPerson.setCountryId(xPath.evaluate("/upiPerson/countryId", document));
        upiPerson.setCountryIdISO2(xPath.evaluate("/upiPerson/countryIdISO2", document));
        upiPerson.setCountryNameShort(xPath.evaluate("/upiPerson/countryNameShort", document));

        return upiPerson;
    }

}
