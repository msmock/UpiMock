package com.smalljetty.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RequestInfo {

    private RequestType requestType;
    private String senderId;
    private String vn;
    private String messageDate;
    private String referenceMessageId;
    private String soapMessageId;
    private String spid;

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

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getVn() {
        return vn;
    }

    public void setVn(String vn) {
        this.vn = vn;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getReferenceMessageId() {
        return referenceMessageId;
    }

    public void setReferenceMessageId(String referenceMessageId) {
        this.referenceMessageId = referenceMessageId;
    }

    public String getSoapMessageId() {
        return soapMessageId;
    }

    public void setSoapMessageId(String soapMessageId) {
        this.soapMessageId = soapMessageId;
    }

    public String getSpid() {
        return spid;
    }

    public void setSpid(String spid) {
        this.spid = spid;
    }
}