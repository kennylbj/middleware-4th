package org.kenny.agent.discovery;

import org.kenny.agent.domain.Agent;

import java.util.List;

public interface Discovery {

    void register(String serviceName, int port);

    void unregister(String serviceName, int port);

    /**
     * Discover agents for specified service name
     * @param serviceName service name for agent
     * @return agent list if discover this service or
     * empty list if do not.
     */
    List<Agent> discover(String serviceName);
}
