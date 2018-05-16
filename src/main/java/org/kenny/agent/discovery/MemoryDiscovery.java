package org.kenny.agent.discovery;

import com.google.common.base.Preconditions;
import org.kenny.agent.domain.Agent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class MemoryDiscovery implements Discovery {
    private final List<Agent> agents = new CopyOnWriteArrayList<>();

    @Override
    public void register(String serviceName, int port) {
        Agent agent = new Agent();
        agent.setPort(port);
        agents.add(agent);
    }

    @Override
    public void unregister(String serviceName, int port) {
        Agent agent = new Agent();
        agent.setPort(port);
        agents.remove(agent);
    }

    @Override
    public List<Agent> discover(String serviceName) {
        Preconditions.checkNotNull(serviceName);
        return agents.stream()
                .filter(agent -> agent.getServiceName().equals(serviceName))
                .collect(Collectors.toList());
    }
}
