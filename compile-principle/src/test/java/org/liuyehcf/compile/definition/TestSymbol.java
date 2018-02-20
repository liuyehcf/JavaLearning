package org.liuyehcf.compile.definition;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.liuyehcf.compile.utils.DefinitionUtils.createTerminator;

public class TestSymbol {
    @Test
    public void testPrint() {
        Map map = new HashMap();
        map.put(new Object(),new Symbol(true,"d"));

        System.out.println(JSON.toJSONString(map));
    }
}
