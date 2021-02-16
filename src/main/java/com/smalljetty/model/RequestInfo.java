package com.smalljetty.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RequestInfo {

    public String vn;
    public String spid;

    public RequestType requestType;

    public String senderId;
    public String messageDate;
    public String referenceMessageId;
    public String soapMessageId;

    public RequestInfo() {
    }

    public RequestInfo(String senderId, String vn, String messageDate, String referenceMessageId, String soapMessageId, RequestType requestType, String spid) {
        this.senderId = senderId;
        this.vn = vn;
        this.messageDate = messageDate;
        this.referenceMessageId = referenceMessageId;
        this.soapMessageId = soapMessageId;
        this.requestType = requestType;
        this.spid = spid;
    }

}