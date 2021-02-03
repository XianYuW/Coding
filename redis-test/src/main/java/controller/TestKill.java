package controller;


import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestKill {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /*@Autowired
    private Redisson redisson;*/

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


}
