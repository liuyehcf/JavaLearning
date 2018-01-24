package org.liuyehcf.mockito;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

interface Service {
    Service func();
}

/**
 * Created by Liuye on 2017/12/24.
 */
@RunWith(MockitoJUnitRunner.class)
public class MockitoDemo {


    @Mock
    private ServiceImpl service;

    @Test
    public void test1() {
        when(service.func()).thenReturn(null);
    }

}

class ServiceImpl implements Service {
    @Override
    public Service func() {
        return this;
    }
}
