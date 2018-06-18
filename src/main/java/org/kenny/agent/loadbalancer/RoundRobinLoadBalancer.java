package org.kenny.agent.loadbalancer;

import org.kenny.agent.domain.Agent;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private int next = 0;

    @Override
    public Agent balance(List<Agent> agents) {
        if (agents.size() != 3) {
            return null;
        }

        // 2 : 3 : 4
        next++;
        if (next == 9) {
            next = 0;
        }
        if (next < 2) {
            return agents.get(0);
        }
        if (next < 5) {
            return agents.get(1);
        }
        return agents.get(2);
    }
}
