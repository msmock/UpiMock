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

    @Override
    public String toString() {

        return vn + ',' + spid + ',' + firstName + ',' + officialName + ',' + birthDate;
    }

    public void setCountryId(String countryId) {
        if (!countryId.isEmpty())
            this.countryId = countryId;
    }

    public void setCountryIdISO2(String countryIdISO2) {
        if (!countryIdISO2.isEmpty())
            this.countryIdISO2 = countryIdISO2;
    }

    public void setCountryNameShort(String countryNameShort) {
        if (!countryNameShort.isEmpty())
            this.countryNameShort = countryNameShort;
    }

    public void setMothersFirstName(String mothersFirstName) {
        if (!mothersFirstName.isEmpty())
            this.mothersFirstName = mothersFirstName;
    }

    public void setMothersOfficialName(String mothersOfficialName) {
        if (!mothersOfficialName.isEmpty())
            this.mothersOfficialName = mothersOfficialName;
    }

    public void setFathersFirstName(String fathersFirstName) {
        if (!fathersFirstName.isEmpty())
            this.fathersFirstName = fathersFirstName;
    }

    public void setFathersOfficialName(String fathersOfficialName) {
        if (!fathersOfficialName.isEmpty())
            this.fathersOfficialName = fathersOfficialName;
    }

    public void setMunicipalityId(String municipalityId) {
        if (!municipalityId.isEmpty())
            this.municipalityId = municipalityId;
    }

    public void setMunicipalityName(String municipalityName) {
        if (!municipalityName.isEmpty())
            this.municipalityName = municipalityName;
    }

    public void setCantonAbbreviation(String cantonAbbreviation) {
        if (!cantonAbbreviation.isEmpty())
            this.cantonAbbreviation = cantonAbbreviation;
    }

    public void setHistoryMunicipalityId(String historyMunicipalityId) {
        if (!historyMunicipalityId.isEmpty())
            this.historyMunicipalityId = historyMunicipalityId;
    }
}
