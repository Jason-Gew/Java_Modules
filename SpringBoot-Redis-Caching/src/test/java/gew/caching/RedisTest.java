package gew.caching;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jason/GeW
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String TEST_KEY = "Test-";
    private static final String TEST_MESSAGE = "{\"Message\": \"This is a Redis Basic Test Message!\"}";

    @Test
    public void basicTest() throws Exception {

        String key = TEST_KEY + System.currentTimeMillis()/1000;
        System.err.println("Redis Basic Test -> Set Key: " + key);
        stringRedisTemplate.opsForValue().set(key, TEST_MESSAGE);
        String result = stringRedisTemplate.opsForValue().get(key);
        System.err.println("Redis Basic Test -> Get Result: " + result);
        Assert.assertEquals(TEST_MESSAGE, result);
        Boolean deleteTestResult = stringRedisTemplate.delete(key);
        Assert.assertNotEquals(deleteTestResult, null);
    }

}