package org.liuyehcf.akka.cluster;

import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.liuyehcf.akka.common.util.AkkaConfigUtils;
import org.liuyehcf.akka.common.util.IPUtils;

import java.util.Collections;

public class ClusterBoot {
    private static final class SeedNode {
        public static void main(String[] args) {
            Config clusterConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("ClusterWithStaticSeedNode.conf"),
                            "127.0.0.1",
                            1100
                    )
            );

            ActorSystem.create("MyClusterSystem", clusterConfig);
        }
    }

    private static final class Member1 {
        public static void main(String[] args) {
            Config clusterConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("ClusterWithStaticSeedNode.conf"),
                            IPUtils.getLocalIp(),
                            1101
                    )
            );

            ActorSystem system = ActorSystem.create("MyClusterSystem", clusterConfig);
            system.actorOf(SimpleClusterListener.props());
        }
    }

    private static final class Member2 {
        public static void main(String[] args) {
            Config clusterConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("ClusterWithStaticSeedNode.conf"),
                            IPUtils.getLocalIp(),
                            1102
                    )
            );

            ActorSystem system = ActorSystem.create("MyClusterSystem", clusterConfig);
            system.actorOf(SimpleClusterListener.props());
        }
    }

    private static final class Member3 {
        public static void main(String[] args) {
            Config clusterConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("ClusterWithNoneSeedNode.conf"),
                            IPUtils.getLocalIp(),
                            1103
                    )
            );

            ActorSystem system = ActorSystem.create("MyClusterSystem", clusterConfig);
            system.actorOf(SimpleClusterListener.props());

            final Cluster cluster = Cluster.get(system);
            // use member2 as seed node
            Address address = new Address("akka.tcp", "MyClusterSystem", IPUtils.getLocalIp(), 1102);
            cluster.joinSeedNodes(Collections.singletonList(address));
        }
    }
}
