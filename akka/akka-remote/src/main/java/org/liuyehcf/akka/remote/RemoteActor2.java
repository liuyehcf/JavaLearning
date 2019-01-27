package org.liuyehcf.akka.remote;

import akka.actor.AbstractActor;
import akka.actor.Props;

/**
 * @author hechenfeng
 * @date 2019/1/25
 */
public class RemoteActor2 extends AbstractActor {

    static Props props() {
        return Props.create(RemoteActor2.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(System.out::println)
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
}
