package com.qdum.jhipsterdemo.distributed.lock;


import com.qdum.jhipsterdemo.distributed.lock.annotation.CacheKeyGenerator;
import com.qdum.jhipsterdemo.distributed.lock.annotation.CacheLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by yaoyt on 2019-03-08.
 *
 * @author yaoyt
 */
@Aspect
@Configuration
public class LockMethodInterceptor {

    private final RedisLockHelper redisLockHelper;
    private final CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    public LockMethodInterceptor(RedisLockHelper redisLockHelper, CacheKeyGenerator cacheKeyGenerator) {
        this.redisLockHelper = redisLockHelper;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @Around("execution(public * *(..)) && @annotation(com.qdum.jhipsterdemo.distributed.lock.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if (StringUtils.isEmpty(lock.prefix())) {
            throw new RuntimeException("lock key don't null...");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(pjp);
        String value = UUID.randomUUID().toString();
        Long failedTime = System.currentTimeMillis() + 1000 * lock.timeout();
        try {
            while (true) {
                // 假设上锁成功，但是设置过期时间失效，以后拿到的都是 false
                final boolean success = redisLockHelper.lock2(lockKey, value, lock.expire(), lock.timeUnit());
                if (success) {
                    try {
                        return pjp.proceed();
                    } catch (Throwable throwable) {
                        throw new RuntimeException("系统异常");
                    }
                } else if (failedTime < System.currentTimeMillis()) {
                    break;
                } else {
                    Thread.sleep(100);
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        } finally {
            // TODO 如果演示的话需要注释该代码;实际应该放开
            redisLockHelper.unlock2(lockKey, value);
        }

    }
}
