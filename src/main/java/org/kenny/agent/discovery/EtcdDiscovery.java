package org.kenny.agent.discovery;

import com.google.common.collect.Lists;
import org.kenny.agent.domain.Agent;

import java.util.List;

public class EtcdDiscovery implements Discovery {
    @Override
    public void register(String serviceName, int port) {

    }

    @Override
    public void unregister(String serviceName, int port) {

    }

    @Override
    public List<Agent> discover(String serviceName) {
        Agent agent = new Agent();
        agent.setHost("localhost");
        agent.setPort(8081);
        return Lists.newArrayList(agent);
    }
}
