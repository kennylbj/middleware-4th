package org.kenny.agent.discovery;

import org.kenny.agent.domain.Agent;

import java.io.Closeable;
import java.util.List;

public interface Discovery extends Closeable {

    void register(String serviceName, int port) throws Exception;

    void unregister(String serviceName, int port) throws Exception;

    /**
     * Discover agents for specified service name
     * @param serviceName service name for agent
     * @return agent list if discover this service or
     * empty list if do not.
     */
    List<Agent> discover(String serviceName);
}
