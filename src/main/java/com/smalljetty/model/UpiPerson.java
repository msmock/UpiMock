package com.smalljetty.model;

public class UpiPerson {
    public String vn;
    public String firstName;
    public String officialName;
    public String sex;
    public String yearMonthDay;
    public String countryId;
    public String countryIdISO2;
    public String countryNameShort;
    public String spid;

    public UpiPerson(String vn, String firstName, String officialName, String sex, String yearMonthDay, String countryId, String countryIdISO2, String countryNameShort, String spid) {
        this.vn = vn;
        this.firstName = firstName;
        this.officialName = officialName;
        this.sex = sex;
        this.yearMonthDay = yearMonthDay;
        this.countryId = countryId;
        this.countryIdISO2 = countryIdISO2;
        this.countryNameShort = countryNameShort;
        this.spid=spid;
    }
}
