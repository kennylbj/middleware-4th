package org.kenny.agent.loadbalancer;

import org.kenny.agent.domain.Agent;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer {
    @Override
    public Agent balance(List<Agent> agents) {
        return agents.get(0);
    }
}
