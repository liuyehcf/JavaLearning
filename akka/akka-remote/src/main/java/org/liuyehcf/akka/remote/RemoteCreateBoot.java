package org.liuyehcf.akka.remote;

import akka.actor.*;
import akka.remote.RemoteScope;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.liuyehcf.akka.common.util.AkkaConfigUtils;
import org.liuyehcf.akka.common.util.IPUtils;

/**
 * @author hechenfeng
 * @date 2019/1/25
 */
public class RemoteCreateBoot {
    private static final class BootRemoteActor1 {
        public static void main(String[] args) {
            Config remoteCreatedConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("remote.conf"),
                            IPUtils.getLocalIp(),
                            10003
                    )
            );

            // just create an actor system
            ActorSystem.create("RemoteSystem1", remoteCreatedConfig);
        }
    }

    private static final class BootRemoteActor2 {
        public static void main(String[] args) {
            Config remoteCreatedConfig = ConfigFactory.parseString(
                    String.format(
                            AkkaConfigUtils.loadConfig("remote.conf"),
                            IPUtils.getLocalIp(),
                            10004
                    )
            );

            ActorSystem system = ActorSystem.create("RemoteSystem2", remoteCreatedConfig);


            // build remote address, choose any of this
            Address addr = new Address("akka.tcp", "RemoteSystem1", IPUtils.getLocalIp(), 10003);
//            Address addr = AddressFromURIString.parse(String.format("akka.tcp://RemoteSystem1@%s:%d", IPUtils.getLocalIp(), 10001));

            // create actor remotely
            ActorRef remoteActor1 = system.actorOf(Props.create(RemoteActor1.class).withDeploy(
                    new Deploy(new RemoteScope(addr))));

            ActorRef remoteActor2 = system.actorOf(RemoteActor2.props(), "RemoteActor2");
            System.out.println(remoteActor2.path());

            // send message to remote actor
            remoteActor1.tell("Hi, I'm remote actor2", remoteActor2);
        }
    }
}
