package org.kenny.agent.domain;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class AgentRequest {
    private static final AtomicLong cnt = new AtomicLong();
    private long requestId;
    // alias for interface
    private String service;
    private String method;
    private String parameterTypesString;
    private String parameter;

    public AgentRequest(){
        requestId = cnt.getAndIncrement();
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public void setParameterTypesString(String parameterTypesString) {
        this.parameterTypesString = parameterTypesString;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
