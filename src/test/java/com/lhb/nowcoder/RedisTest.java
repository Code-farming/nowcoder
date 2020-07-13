package com.lhb.nowcoder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:hash";
        redisTemplate.opsForHash().put(redisKey, "username", "lili");
        redisTemplate.opsForHash().put(redisKey, "id", 1);

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));

    }

    @Test
    public void testList() {
        String redisKey = "test:list";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 1));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 1));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().size(redisKey));

    }

    @Test
    public void testSet() {
        String redisKey = "test:set";
        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "诸葛亮", "赵云", "黄宗", "马超");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        System.out.println(redisTemplate.opsForSet().size(redisKey));
    }

    @Test
    public void testZSet() {
        String redisKey = "test:ZSet";
        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);


        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));

    }

    @Test
    public void testKey() {
        redisTemplate.delete("test:hash");
        System.out.println(redisTemplate.hasKey("test:hash"));
        redisTemplate.expire("test:list", 5, TimeUnit.SECONDS);
    }


    @Test
    // 多次访问同一个key
    public void testBoundOperation() {
        String redisKey = "test:count";
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(redisKey);
        boundValueOperations.increment();
        boundValueOperations.increment();
        boundValueOperations.increment();
        boundValueOperations.increment();
        boundValueOperations.increment();
        System.out.println(boundValueOperations.get());
    }

    @Test
    // 编程式事务管理
    public void testTransaction() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                // 开启
                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey, "zhangsan");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");

                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec();
            }
        });
    }


}
