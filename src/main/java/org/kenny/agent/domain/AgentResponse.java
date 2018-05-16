package org.kenny.agent.domain;

import lombok.Data;

@Data
public class AgentResponse {
    private long requestId;
    private int result;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
