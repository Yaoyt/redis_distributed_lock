package com.qdum.jhipsterdemo.distributed.lock.annotation;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by yaoyt on 2019-03-08.
 * key 的生成器
 * @author yaoyt
 */
public interface CacheKeyGenerator {

    /**
     * 获取AOP参数,生成指定缓存Key
     *
     * @param pjp PJP
     * @return 缓存KEY
     */
    String getLockKey(ProceedingJoinPoint pjp);

}
