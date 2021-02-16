package com.smalljetty.model;

public class UpiPerson {

    public String vn;
    public String spid;

    // person data
    public String firstName;
    public String officialName;
    public String sex;
    public String birthDate;

    // nationality data
    public String countryId = "8100";
    public String countryIdISO2 = "CH";
    public String countryNameShort = "SWITZERLAND";

    // mothers name
    public String mothersFirstName = "Sabine";
    public String mothersOfficialName = "Moser";

    // fathers name
    public String fathersFirstName = "Reto";
    public String fathersOfficialName = "Moser";

    // birth place data
    public String municipalityId = "198";
    public String municipalityName = "Uster";
    public String cantonAbbreviation = "ZH";
    public String historyMunicipalityId = "10940";

    public UpiPerson(){ };
}
