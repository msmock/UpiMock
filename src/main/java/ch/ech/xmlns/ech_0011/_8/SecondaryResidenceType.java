
package ch.ech.xmlns.ech_0011._8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ch.ech.xmlns.ech_0007._5.SwissMunicipalityType;


/**
 * <p>Java class for secondaryResidenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="secondaryResidenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mainResidence" type="{http://www.ech.ch/xmlns/eCH-0007/5}swissMunicipalityType"/>
 *         &lt;element name="secondaryResidence">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.ech.ch/xmlns/eCH-0011/8}residenceDataType">
 *                 &lt;sequence>
 *                   &lt;element name="reportingMunicipality" type="{http://www.ech.ch/xmlns/eCH-0007/5}swissMunicipalityType"/>
 *                   &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                   &lt;element name="comesFrom" type="{http://www.ech.ch/xmlns/eCH-0011/8}destinationType"/>
 *                   &lt;element name="dwellingAddress" type="{http://www.ech.ch/xmlns/eCH-0011/8}dwellingAddressType"/>
 *                   &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                   &lt;element name="goesTo" type="{http://www.ech.ch/xmlns/eCH-0011/8}destinationType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "secondaryResidenceType", propOrder = {
    "mainResidence",
    "secondaryResidence"
})
public class SecondaryResidenceType {

    @XmlElement(required = true)
    protected SwissMunicipalityType mainResidence;
    @XmlElement(required = true)
    protected SecondaryResidenceType.SecondaryResidence secondaryResidence;

    /**
     * Gets the value of the mainResidence property.
     * 
     * @return
     *     possible object is
     *     {@link SwissMunicipalityType }
     *     
     */
    public SwissMunicipalityType getMainResidence() {
        return mainResidence;
    }

    /**
     * Sets the value of the mainResidence property.
     * 
     * @param value
     *     allowed object is
     *     {@link SwissMunicipalityType }
     *     
     */
    public void setMainResidence(SwissMunicipalityType value) {
        this.mainResidence = value;
    }

    /**
     * Gets the value of the secondaryResidence property.
     * 
     * @return
     *     possible object is
     *     {@link SecondaryResidenceType.SecondaryResidence }
     *     
     */
    public SecondaryResidenceType.SecondaryResidence getSecondaryResidence() {
        return secondaryResidence;
    }

    /**
     * Sets the value of the secondaryResidence property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecondaryResidenceType.SecondaryResidence }
     *     
     */
    public void setSecondaryResidence(SecondaryResidenceType.SecondaryResidence value) {
        this.secondaryResidence = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.ech.ch/xmlns/eCH-0011/8}residenceDataType">
     *       &lt;sequence>
     *         &lt;element name="reportingMunicipality" type="{http://www.ech.ch/xmlns/eCH-0007/5}swissMunicipalityType"/>
     *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *         &lt;element name="comesFrom" type="{http://www.ech.ch/xmlns/eCH-0011/8}destinationType"/>
     *         &lt;element name="dwellingAddress" type="{http://www.ech.ch/xmlns/eCH-0011/8}dwellingAddressType"/>
     *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *         &lt;element name="goesTo" type="{http://www.ech.ch/xmlns/eCH-0011/8}destinationType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SecondaryResidence
        extends ResidenceDataType
    {


    }

}
