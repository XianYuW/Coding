package test;

import redis.clients.jedis.Jedis;

public class TestJedis {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("121.4.196.77", 6379);
        jedis.auth("JIjun@666");
        String pong = jedis.ping();
        System.out.println(pong);
    }
}
