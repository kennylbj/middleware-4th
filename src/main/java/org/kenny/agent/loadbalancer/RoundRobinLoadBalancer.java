package org.kenny.agent.loadbalancer;

import org.kenny.agent.domain.Agent;

import java.util.List;
import java.util.Random;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private final Random random = new Random();

    @Override
    public Agent balance(List<Agent> agents) {
        if (agents.size() != 3) {
            return agents.get(0);
        }
        // range is [0, 1, 2, 3, 4, 5]
        int range = random.nextInt(6);
        if (range < 1) {
            return agents.get(0);
        }
        if (range < 3) {
            return agents.get(1);
        }
        return agents.get(2);
    }
}
