package org.liuyehcf.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by HCF on 2017/12/13.
 */
public class RedisClientDemo {
    public static void main(String[] args) {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping());

        jedis.set("Name", "Chenfeng");
        System.out.println(jedis.get("Name"));
        System.out.println(jedis.keys("*"));
    }
}

