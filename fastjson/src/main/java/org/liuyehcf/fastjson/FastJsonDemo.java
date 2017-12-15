package org.liuyehcf.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.liuyehcf.fastjson.entity.InnerEntity;
import org.liuyehcf.fastjson.entity.MiddleEntity;
import org.liuyehcf.fastjson.entity.OuterEntity;

/**
 * Created by Liuye on 2017/12/15.
 */
public class FastJsonDemo {
    public static void main(String[] args) {
        OuterEntity<MiddleEntity> outerEntity = new OuterEntity<>();
        MiddleEntity<InnerEntity> middleEntity = new MiddleEntity<>();
        InnerEntity innerEntity = new InnerEntity();

        innerEntity.setId(1);
        innerEntity.setName("InnerEntity");

        middleEntity.setId(2);
        middleEntity.setName("middleEntity");
        middleEntity.setInnerEntity(innerEntity);

        outerEntity.setId(3);
        outerEntity.setName("outerEntity");
        outerEntity.setMiddleEntity(middleEntity);


        String json = JSON.toJSONString(outerEntity);

        System.out.println(json);

        OuterEntity<MiddleEntity<InnerEntity>> parsedOuterEntity = JSON.parseObject(
                json,
                new TypeReference<OuterEntity<MiddleEntity<InnerEntity>>>() {
                });

        System.out.println(parsedOuterEntity.getMiddleEntity().getClass().getName());
    }
}
