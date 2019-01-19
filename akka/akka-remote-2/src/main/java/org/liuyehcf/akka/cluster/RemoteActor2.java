package org.liuyehcf.akka.cluster;

import akka.actor.*;

/**
 * @author chenlu
 * @date 2019/1/17
 */
public class RemoteActor2 extends AbstractActor {

    private static Props props() {
        return Props.create(RemoteActor2.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Object.class, System.out::println)
                .build();
    }

    @Override
    public void preStart() {
        System.out.println("remote actor2 start");
    }

    @Override
    public void postStop() {
        System.out.println("remote actor2 stop");
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("RemoteSystem2");
        ActorRef remoteActor2 = system.actorOf(props(), "RemoteActor2");

        ActorSelection remoteActor1 = system.actorSelection("akka.tcp://RemoteSystem1@127.0.0.1:2552/user/RemoteActor1");

        remoteActor1.tell("hello, I'm remote actor2", remoteActor2);
    }
}
