package com.nivelle.spring.springmvc;

import com.nivelle.spring.springmvc.filter.MyCrossFilter;
import com.nivelle.spring.springmvc.filter.MyFilter1;
import com.nivelle.spring.springmvc.filter.MyFilter2;
import com.nivelle.spring.springmvc.servlet.MyServlet1;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * TODO:DOCUMENT ME!
 *
 * @author fuxinzhong
 * @date 2020/08/04
 */
@Configuration
public class MyBaseConfig {

    /**
     * 自定义消息转换器
     *
     * @return
     */
    @Bean
    public MyHttpMessageConverter converter() {
        return new MyHttpMessageConverter();
    }

    /**
     * 自定义servle有三种注入方式
     * <p>
     * 1.ServletRegistrationBean
     * <p>
     * 2.@WebServlet
     * <p>
     * 3.ServletContextInitializer
     */
    @Bean
    public ServletRegistrationBean registerServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
                new MyServlet1(), "/registerServlet");
        servletRegistrationBean.addInitParameter("desc", "nivele love jessy");
        System.out.println("自定义servlet");
        return servletRegistrationBean;
    }


    /**
     * 请求过滤器
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    @DependsOn("myFilter2")//初始化顺序控制
    public FilterRegistrationBean filterRegister() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyFilter1());
        filterRegistrationBean.addUrlPatterns("/*");
        //数字越小，优先级越高，指的是执行顺序，加载顺序按照先后，若需要修改，可使用注解 @DependsOn
        filterRegistrationBean.setOrder(2);
        System.out.println("过滤器1注册完成");
        return filterRegistrationBean;
    }

    /**
     * 请求过滤器2先与过滤器1执行
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean(name = "myFilter2")
    public FilterRegistrationBean filterRegister2() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyFilter2());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        System.out.println("过滤器2注册完成");
        return filterRegistrationBean;
    }

    /**
     * 请求过滤器2先与过滤器1执行
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean(name = "crossFilter")
    public FilterRegistrationBean filterCross() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyCrossFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        System.out.println("跨域过滤器");
        return filterRegistrationBean;
    }
}
