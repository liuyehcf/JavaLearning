package org.liuyehcf.akka.remote;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.liuyehcf.akka.common.util.AkkaConfigUtils;

/**
 * @author chenlu
 * @date 2019/1/25
 */
public class LocalCreateBoot {
    private static final class BootRemoteActor1 {
        public static void main(String[] args) {
            Config localCreatedConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("remote.conf"),
                            "127.0.0.1",
                            10001
                    )
            );

            ActorSystem system = ActorSystem.create("RemoteSystem1", localCreatedConfig);
            ActorRef remoteActor1 = system.actorOf(RemoteActor1.props(), "RemoteActor1");
            System.out.println(remoteActor1.path());
        }
    }

    private static final class BootRemoteActor2 {
        public static void main(String[] args) {
            Config localCreatedConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("remote.conf"),
                            "127.0.0.1",
                            10002
                    )
            );

            ActorSystem system = ActorSystem.create("RemoteSystem2", localCreatedConfig);

            // search actor remotely
            ActorSelection remoteActor1 = system.actorSelection(
                    String.format(
                            "akka.tcp://RemoteSystem1@%s:%d/user/RemoteActor1",
                            "127.0.0.1",
                            10001)
            );

            ActorRef remoteActor2 = system.actorOf(RemoteActor2.props(), "RemoteActor2");
            System.out.println(remoteActor2.path());

            // send message to remote actor
            remoteActor1.tell("Hi, I'm remote actor2", remoteActor2);
        }
    }
}
