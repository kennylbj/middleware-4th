package org.kenny.agent.discovery;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import org.kenny.agent.domain.Agent;

import javax.annotation.concurrent.GuardedBy;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * jetcd don't support async watch
 */
public class EtcdDiscovery implements Discovery {
    private static final String ROOT = "dubbomesh";
    private static final String SERVICE_NAME = "com.alibaba.dubbo.performance.demo.provider.IHelloService";
    private static final int LEASE_TIME = 30;
    private final Client client;
    private final long leaseId;

    // use volatile to guarantee the visibility of agents
    @GuardedBy("this")
    private volatile List<Agent> agents;

    public EtcdDiscovery() {
        String url = System.getProperty("etcd.url");
        this.client = Client.builder().endpoints(url).build();
        try {
            this.leaseId = client.getLeaseClient().grant(LEASE_TIME).get().getID();

            // don't need to spawn a executor since it has done by lease client
            client.getLeaseClient().keepAlive(leaseId);

            String type = System.getProperty("type");
            if ("provider".equals(type)){
                int port = Integer.valueOf(System.getProperty("server.port"));
                register(SERVICE_NAME, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fail to start etcd discovery");
        }
    }
    @Override
    public void register(String serviceName, int port) throws Exception {
        String hostIp = InetAddress.getLocalHost().getHostAddress();
        String strKey = "/" + ROOT + "/" + serviceName + "/" + hostIp + ":" + port;
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString("");
        client.getKVClient().put(key, val, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
    }

    @Override
    public void unregister(String serviceName, int port) throws Exception {
        String hostIp = InetAddress.getLocalHost().getHostAddress();
        String strKey = "/" + ROOT + "/" + serviceName + "/" + hostIp + ":" + port;
        ByteSequence key = ByteSequence.fromString(strKey);
        client.getKVClient().delete(key).get();
    }

    @Override
    public List<Agent> discover(String serviceName) {
        if (agents == null) {
            synchronized (EtcdDiscovery.class) {
                if (agents == null) {
                    agents = new ArrayList<>();
                    String strKey = "/" + ROOT + "/" + serviceName;
                    ByteSequence key = ByteSequence.fromString(strKey);
                    GetResponse response = null;
                    try {
                        response = client.getKVClient().get(key, GetOption.newBuilder().withPrefix(key).build()).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    for (com.coreos.jetcd.data.KeyValue kv : response.getKvs()) {
                        String s = kv.getKey().toStringUtf8();
                        int index = s.lastIndexOf("/");
                        String endpointStr = s.substring(index + 1,s.length());

                        String host = endpointStr.split(":")[0];
                        int port = Integer.valueOf(endpointStr.split(":")[1]);

                        Agent agent = new Agent();
                        agent.setHost(host);
                        agent.setPort(port);
                        agent.setServiceName(serviceName);
                        agents.add(agent);
                    }
                }
            }
        }
        return agents;

    }

    @Override
    public void close() throws IOException {
        // this will close lease client and kv client implicitly
        client.close();
    }

}
