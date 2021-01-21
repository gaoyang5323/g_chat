package com.kakuiwong.httpserver.httpconfig.web;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.kakuiwong.httpserver.httpconfig.intercepter.AppAuthInterceptor;
import com.kakuiwong.httpserver.httpconfig.intercepter.DefaultFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : gaoyang
 */
@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    @Autowired
    AppAuthInterceptor appAuthInterceptor;

    @Bean
    public FilterRegistrationBean filterRegistrationBeanDefaultFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new DefaultFilter());
        bean.setUrlPatterns(Arrays.asList("/*"));
        bean.setName("defaultFilter");
        bean.setOrder(-1);
        Map map = new HashMap();
        map.put("xss", "yes");
        bean.setInitParameters(map);
        return bean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appAuthInterceptor).
                addPathPatterns("/**").
                excludePathPatterns("/monitor/**", "/swagger*", "/swagger-ui.html",
                        "/swagger-resources/**", "/webjars/**");
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        };
    }

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        };
    }

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON_UTF8));
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect);
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        config.setSerializeConfig(serializeConfig);
        converter.setFastJsonConfig(config);
        return converter;
    }
}
