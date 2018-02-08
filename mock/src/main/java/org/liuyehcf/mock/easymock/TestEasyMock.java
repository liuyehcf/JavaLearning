package org.liuyehcf.mock.easymock;

import org.easymock.EasyMock;

/**
 * Created by HCF on 2017/8/28.
 */


interface Human {
    boolean isMale(String name);
}

public class TestEasyMock {
    private int i;


    public static void main(String[] args) {
        Human mock = EasyMock.createMock(Human.class);


        EasyMock.expect(mock.isMale("Bob")).andReturn(true);
        EasyMock.expect(mock.isMale("Alice")).andReturn(true);

        EasyMock.replay(mock);

        System.out.println(mock.isMale("Bob"));
        System.out.println(mock.isMale("Alice"));

        try {
            System.out.println(mock.isMale("Robot"));
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }


    }
}
