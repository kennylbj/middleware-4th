package org.kenny.agent;

import io.netty.channel.Channel;
import org.kenny.agent.domain.Agent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;

@ThreadSafe
public class ThreadLocalHolder {
    private final ThreadLocal<Map<Agent, Channel>> agentChannelMapLocal;
    private final ThreadLocal<Map<Long, Channel>> idChannelMapLocal;

    private static class Holder {
        private static ThreadLocalHolder singleton = new ThreadLocalHolder();
    }

    private ThreadLocalHolder() {
        agentChannelMapLocal = ThreadLocal.withInitial(HashMap::new);
        idChannelMapLocal = ThreadLocal.withInitial(HashMap::new);
    }

    public static ThreadLocalHolder getInstance(){
        return Holder.singleton;
    }

    public ThreadLocal<Map<Agent, Channel>> getAgentChannelMapLocal() {
        return agentChannelMapLocal;
    }

    public ThreadLocal<Map<Long, Channel>> getIdChannelMapLocal() {
        return idChannelMapLocal;
    }
}
