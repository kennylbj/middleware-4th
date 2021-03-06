package org.kenny.agent.domain;

import lombok.Data;

@Data
public class Agent {
    private String host;
    private int port;
    private String serviceName;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return host + ":" + port + "/" + serviceName;
    }
}
