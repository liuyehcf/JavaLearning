package org.liuyehcf.akka.cluster;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * @author chenlu
 * @date 2019/1/17
 */
public class RemoteActor1 extends AbstractActor {

    private static Props props() {
        return Props.create(RemoteActor1.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Object.class, msg -> {
                    System.out.println(msg);
                    sender().tell("hi, I'm remote actor1", self());
                })
                .build();
    }

    @Override
    public void preStart() {
        System.out.println("remote actor1 start");
    }

    @Override
    public void postStop() {
        System.out.println("remote actor2 stop");
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("RemoteSystem1");
        ActorRef remoteActor = system.actorOf(props(), "RemoteActor1");
        System.out.println(remoteActor.path());
    }

}
