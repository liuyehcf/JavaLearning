package org.liuyehcf.akka.remote;

import akka.actor.AbstractActor;
import akka.actor.Props;

/**
 * @author hechenfeng
 * @date 2019/1/17
 */
public class RemoteActor1 extends AbstractActor {

    static Props props() {
        return Props.create(RemoteActor1.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(msg -> {
                    System.out.println(msg);
                    sender().tell("Hi, I'm remote actor1", self());
                })
                .build();
    }

    @Override
    public void preStart() {
        System.out.println("remote actor1 start");
    }

    @Override
    public void postStop() {
        System.out.println("remote actor1 stop");
    }
}
