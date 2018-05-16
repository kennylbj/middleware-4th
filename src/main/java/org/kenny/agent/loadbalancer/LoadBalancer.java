package org.kenny.agent.loadbalancer;

import org.kenny.agent.domain.Agent;

import java.util.List;

public interface LoadBalancer {

    Agent balance(List<Agent> agents);

}
