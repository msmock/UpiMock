package com.smalljetty.xmlcheck;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class XmlCheck {

    private static String envPefix = "src/main/java/"; //for dev.
    //private static String envPefix="classes/"; //for maven build

    public static void main(String[] args) throws Exception {

        File dir = new File(envPefix + "com/smalljetty/upiPersons");
        File[] directoryListing = dir.listFiles();

        if (directoryListing == null)
            throw new Exception("No files found in dir "+dir.getAbsolutePath());

        for (File xmlFile : directoryListing) {

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile.getAbsolutePath());

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            String document_spid = xPath.evaluate("/upiPerson/SPID", document);
            String document_vn = xPath.evaluate("/upiPerson/vn", document);

            //do checks
            checkFilename(document_vn, xmlFile.getName());
            checkAhv(document_vn);
            checkSpid(document_spid);
            checkIfAhvAndSpidIsUnique(document_vn, document_spid, directoryListing);
        }

    }

    private static void checkFilename(String vn, String filename) {
        if (!(vn + ".xml").equals(filename)) {
            System.out.println(vn + ": vn and filename do not match");
        }
    }

    private static void checkAhv(String ahv) {
        //check length
        if (ahv.length() != 13) {
            System.out.println(ahv + ": ahv length not 13");
        }

        //check if it has number
        for (int i = 0; i != ahv.length(); i++) {
            if (!Character.isDigit(ahv.charAt(i))) {
                System.out.println(ahv + ": ahv contains non digit characters");
            }
        }

    }

    private static void checkSpid(String spid) {
        //check length
        if (spid.length() != 18) {
            System.out.println(spid + ": ahv length not 13");
        }

        //check if it has number
        for (int i = 0; i != spid.length(); i++) {
            if (!Character.isDigit(spid.charAt(i))) {
                System.out.println(spid + ": ahv contains non digit characters");
            }
        }
    }

    private static void checkIfAhvAndSpidIsUnique(String ahv, String spid, File[] directoryListing) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        int ahvCount = 0;
        int spidCount = 0;

        for (File xmlFile : directoryListing) {

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(xmlFile.getAbsolutePath());

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            String document_spid = xPath.evaluate("/upiPerson/SPID", document);
            String document_vn = xPath.evaluate("/upiPerson/vn", document);

            if (document_vn.equals(ahv)) {
                ahvCount++;
                if (ahvCount > 1) {
                    System.out.println(ahv + ": ahv is not unique");
                }
            }

            if (document_spid.equals(spid)) {
                spidCount++;
                if (spidCount > 1) {
                    System.out.println(spid + ": spid is not unique");
                }
            }

        }
    }

}
