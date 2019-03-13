package com.qdum.jhipsterdemo.service;

import com.qdum.jhipsterdemo.distributed.lock.annotation.CacheLock;
import com.qdum.jhipsterdemo.distributed.lock.annotation.CacheParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by yaoyt on 2019-03-12.
 *
 * @author yaoyt
 */
@Service
public class BookService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @CacheLock(prefix = "counts")
    public void count(@CacheParam(name = "subKey") String subKey, String key2) {
        String ct = stringRedisTemplate.opsForValue().get("ct");
        if (StringUtils.isEmpty(ct)) {
            ct = "0";
        }
        Long count = Long.valueOf(ct);
        count++;
        stringRedisTemplate.opsForValue().set("ct", count.toString());
    }
}
