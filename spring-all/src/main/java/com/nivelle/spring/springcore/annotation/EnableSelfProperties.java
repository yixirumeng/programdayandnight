package com.nivelle.spring.springcore.annotation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 开启 @ConfigurationProperties 自动注解功能
 *
 * @author nivelle
 * @date 2020/01/16
 */

/**
 * 将 SelfProperties 做为一个Bean注册到容器当中
 */
@EnableConfigurationProperties(value = SelfProperties.class)
public class EnableSelfProperties {

}
