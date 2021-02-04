package controller;


import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestKill {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Redisson redisson;

    @RequestMapping("kill")
    //synchronized有一个问题，因为他锁的是一个进程下的并发，多个进程则会失效
    public synchronized String kill(){
        //从redis获取库存容量
        int count = Integer.parseInt(stringRedisTemplate.opsForValue().get("phone"));
        //判断是否能够秒杀
        if(count>0){
            count--;
            //库存减少后，再次将库存的值保存到redis
            stringRedisTemplate.opsForValue().set("phone",String.valueOf(count));
            System.out.println(String.format("库存减少，剩余%s",count));
        }else{
            System.out.println("库存不足");
        }
        return "over";
    }
    @RequestMapping("kill2")
    //synchronized有一个问题，因为他锁的是一个进程下的并发，多个进程则会失效
    public synchronized String kill2(){
        // 定义商品id
        String productKey = "HUAWEI-P40";
        // 通过redisson获取锁
        RLock rLock = redisson.getLock(productKey);
        // 底层源码就是集成了setnx，过期时间等操作
        // 上锁（过期时间为30秒）
        rLock.lock(30, TimeUnit.SECONDS);
        try{
            // 1.从redis中获取 手机的库存数量
            int phoneCount =
                    Integer.parseInt(stringRedisTemplate.opsForValue().get("phone"));
            // 2.判断手机的数量是否够秒杀的
            if (phoneCount > 0) {
                phoneCount--;
            // 库存减少后，再将库存的值保存回redis
                stringRedisTemplate.opsForValue().set("phone", phoneCount + "");
                System.out.println("库存-1，剩余：" + phoneCount);
            } else {
                System.out.println("库存不足！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 释放锁
            rLock.unlock();
        }
        return "over!";
    }


    @Bean
    public Redisson redisson(){
        Config config = new Config();
        //使用单个redis服务器
        config.useSingleServer().setAddress("redis://121.4.196.77:6379").setPassword("JIjun@666").setDatabase(0);
        //使用redis集群
        //config.useClusterServers().setScanInterval(2000).addNodeAddress("redis://192.168.31.151:6379","redis://192.168.31.152","redis://192.168.31.153");
        return (Redisson)Redisson.create(config);
    }


}
