package org.kenny.agent.loadbalancer;

import org.kenny.agent.domain.Agent;

import java.util.List;
import java.util.Random;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private final Random random = new Random();

    @Override
    public Agent balance(List<Agent> agents) {
        int size = agents.size();
        return agents.get(random.nextInt(size));
    }
}
