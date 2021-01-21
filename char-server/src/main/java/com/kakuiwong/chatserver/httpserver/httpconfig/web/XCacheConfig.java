package com.kakuiwong.chatserver.httpserver.httpconfig.web;

import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @author: gaoyang
 * @Description:
 */
@Configuration
public class XCacheConfig {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean
    public EhCacheCacheManager eCacheCacheManager(EhCacheManagerFactoryBean bean) {
        return new EhCacheCacheManager(bean.getObject());
    }
}
