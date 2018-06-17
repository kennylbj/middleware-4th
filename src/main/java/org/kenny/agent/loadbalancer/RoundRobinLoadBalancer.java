package org.kenny.agent.loadbalancer;

import org.kenny.agent.domain.Agent;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private int next = 0;

    @Override
    public Agent balance(List<Agent> agents) {
        if (agents.size() != 3) {
            return agents.get(0);
        }
        // next is [0, 1, 2, 3, 4, 5]
        next++;
        if (next == 6) {
            next = 0;
        }
        if (next < 1) {
            return agents.get(0);
        }
        if (next < 3) {
            return agents.get(1);
        }
        return agents.get(2);
    }
}
