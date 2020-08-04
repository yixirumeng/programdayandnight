package com.nivelle.spring.test;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nivelle.spring.GsonUtils;
import com.nivelle.spring.configbean.CommonConfig;
import com.nivelle.spring.pojo.*;
import com.nivelle.spring.springboot.dao.ActivityDaoImpl;
import com.nivelle.spring.springboot.mapper.ActivityPvMapper;
import com.nivelle.spring.springcore.aop.MyService;
import com.nivelle.spring.springcore.basics.InitSpringBean;
import com.nivelle.spring.springcore.basics.XmlBean;
import com.nivelle.spring.springcore.factorybean.*;
import com.nivelle.spring.springcore.listener.springevent.MyEvent;
import com.nivelle.spring.springmvc.MyHandlerMethodArgumentResolver;
import com.nivelle.spring.springmvc.MyHandlerMethodReturnValueHandler;
import com.nivelle.spring.springmvc.MyHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
public class SpringAllTestController {

    @Autowired
    CommonConfig commonConfig;
    @Autowired
    ActivityPvMapper activityPvMapper;

    @Autowired
    ActivityDaoImpl activityDao;

    @Autowired
    MyFactoryBean myFactoryBean;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MyService myService;
    @Autowired
    InitSpringBean initSpringBean;
    /**
     * 获取上下文
     */
    @Autowired
    WebApplicationContext webApplicationConnect;

    @PostMapping("/config")
    @ResponseBody
    public Object config(@RequestBody User user) {
        String desc = commonConfig.getDesc();
        System.out.println(desc);
        System.out.println(user.name);
        HashMap result = Maps.newHashMap();
        result.put("age",user.getAge());
        result.put("name",user.getName());
        return result;
    }

    @RequestMapping("/argument")
    @ResponseBody
    public String argument() {
        return "nivelle";
    }

    @RequestMapping("/return")
    @ResponseBody
    public Map returnValue() {
        Map map = Maps.newHashMap();
        map.put(1,2);
        return map;
    }
    /**
     * 优雅停机
     *
     * @return
     */
    @RequestMapping("/graceFull")
    @ResponseBody
    public String graceFull() {
        try {
            System.out.println("等待过程中终止进程,但是依然等待当前任务执行完毕");
            Thread.sleep(30000);
        } catch (Exception e) {
        }
        System.out.println("优雅停机执行完毕");
        return "stop success";
    }


    /**
     * jdbcTemplate 实践
     *
     * @return
     */
    @RequestMapping("/activityPv/{id}")
    @ResponseBody
    public ActivityPvEntity getActivityPv(@PathVariable String id) {
        ActivityPvEntity activityPvEntity = activityDao.getActivitiesById(Long.valueOf(id));
        System.out.println("activityPv is:" + activityPvEntity);
        return activityPvEntity;
    }

    /**
     * 原型返回
     *
     * @return
     */
    @RequestMapping("/activityPvs")
    @ResponseBody
    public Object getActivityPvs() {
        List<Map<String, Object>> activityList = activityDao.getActivityList();
        System.out.println("activityList is:" + activityList);
        return activityList;
    }

    /**
     * 自动映射
     *
     * @return
     */
    @RequestMapping("/activityPvs2")
    @ResponseBody
    public Object getActivityPvs2() {
        List<ActivityPvEntity> activityList = activityDao.getActivityList2();
        System.out.println("activityList is:" + activityList);
        return activityList;
    }

    /**
     * 自定义对象映射
     *
     * @return
     */
    @RequestMapping("/activityPvs3")
    @ResponseBody
    public Object getActivityPvs3() {
        List<ActivityPvEntity> activityList = activityDao.getActivityList3();
        System.out.println("activityList is:" + activityList);
        return activityList;
    }

    /**
     * 自定义对象映射
     *
     * @return
     */
    @RequestMapping("/updateActivity/{id}")
    @ResponseBody
    public Object changeActivityPv(@PathVariable String id) {
        ActivityPvEntity activityPvEntity = activityDao.getActivitiesForUpdate(Long.valueOf(id));
        System.out.println(activityPvEntity);
        int changeCount = activityDao.updateActivityPv(activityPvEntity);
        return changeCount;
    }

    /**
     * 自定义对象映射
     *
     * @return
     */
    @RequestMapping("/selectForUpdate/{id}")
    @ResponseBody
    public Object getActivityPvForUpdate(@PathVariable String id) {
        ActivityPvEntity activityPvEntity = activityDao.getActivitiesForUpdate(Long.valueOf(id));
        return activityPvEntity;
    }

    /**
     * 自定义事件发布
     *
     * @return
     */
    @RequestMapping("/publishEvent")
    public void publishEvent() {
        webApplicationConnect.publishEvent(new MyEvent("你好"));
        return;
    }

    /**
     * 单实例测试
     *
     * @return
     */
    @RequestMapping("/singletonBean")
    public Object singletonTest() {
        Dog dog = (Dog) webApplicationConnect.getBean("bigDog");
        System.out.println(dog.getClass().getName());
        Dog dog2 = (Dog) webApplicationConnect.getBean("bigDog");
        if (dog == dog2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 多实例测试
     *
     * @return
     */
    @RequestMapping("/prototypeBean")
    public Object prototypeBeanTest() {
        Dog dog = (Dog) webApplicationConnect.getBean("buDingDog");
        Dog dog2 = (Dog) webApplicationConnect.getBean("buDingDog");
        if (dog == dog2) {
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping("/registerBean")
    public Object registerBean() {
        Cat cat = (Cat) webApplicationConnect.getBean("com.nivelle.guide.model.Cat");
        System.out.println(cat);
        return cat;
    }

    @RequestMapping("/ok")
    public Object test() {
        System.out.println("dubbo provider is ok");
        /**
         * springboot默认属性未设置值时为null,可设置为""
         */
        //UserInfo userInfo = new UserInfo();
        Object object = webApplicationConnect.getBean("userInfo");
        return object;
    }

    /**
     * 通过xml配置文件导入不能自动扫描到的实例
     *
     * @return
     */
    @RequestMapping("xml")
    public Object testXmlService() {
        Object xmlBeanService = webApplicationConnect.getBean("xmlService");
        XmlBean xmlBeanService1 = (XmlBean) xmlBeanService;
        return xmlBeanService1.helloXmlService();
    }


    /**
     * 初始化测试
     *
     * @return
     */
    @RequestMapping("/init")
    @ResponseBody
    public Object myInitSpringBean() {

        String name = initSpringBean.getName();
        int age = initSpringBean.getAge();

        return name + age;
    }


    @RequestMapping("/defaultParameter")
    public ModelAndView defaultParameter(HttpServletRequest request,
                                         HttpServletResponse response,
                                         HttpSession session,
                                         Model model,
                                         ModelMap modelMap) throws Exception {
        request.setAttribute("requestParameter", "request类型");
        response.getWriter().write("nivelle's response");

        session.setAttribute("sessionParameter", "session类型");
        //ModelMap是Model接口的一个实现类，作用是将Model数据填充到request域
        //即使使用Model接口，其内部绑定还是由ModelMap来实现
        model.addAttribute("modelParameter", "model类型");
        modelMap.addAttribute("modelMapParameter", "modelMap类型");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/success");
        return mv;
    }

    /**
     * session/cookie
     *
     * @param session
     * @return
     */
    @RequestMapping("/session/cookie")
    public ModelAndView getSession(HttpSession session, HttpServletRequest request) {

        String sessionContent = session.getAttribute("sessionParameter").toString();
        System.out.println("=====" + sessionContent);

        System.out.println("-----" + request.getCookies()[0].getName() + request.getCookies()[0].getValue());

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/success");
        return mv;
    }

    /**
     * 自定义参数转换器测试,不依赖@RequestBody和 @ResponseBody
     * <p>
     * {@link MyHandlerMethodArgumentResolver}
     * {@link MyHandlerMethodReturnValueHandler}
     *
     * @param properties
     * @return
     */
    @RequestMapping(value = "convertSelf", consumes = "text/properties")
    public Properties getconvertSelf(Properties properties) {
        System.out.println("入参被解析:properties={}" + properties);
        return properties;
    }

    /**
     * 自定义参数转换器测试,依赖@RequestBody和 @ResponseBody
     * <p>
     * {@link MyHttpMessageConverter}
     *
     * @param properties
     * @return
     */
    @RequestMapping(value = "convertAnnotation", produces = "text/properties", consumes = "text/properties")
    @ResponseBody
    public Properties getConvertAnnotation(@RequestBody Properties properties) {
        System.out.println("入参被解析:properties的类型:" + properties.getClass().getSimpleName());
        return properties;
    }


    /**
     * 工厂类获取bean
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/timeline")
    public List<Object> getObject() throws Exception {
        //直接通过#getObject获取实例
        TimeLine timeLine = myFactoryBean.getObject();

        //通过Spring上下文获取实例
        TimeLine timeLine1 = (TimeLine) applicationContext.getBean("myFactoryBean");
        //MyFactoryBean
        MyFactoryBean bean = (MyFactoryBean) applicationContext.getBean("&myFactoryBean");
        List<Object> time = Lists.newArrayList();
        time.add(timeLine);
        time.add(timeLine1);
        time.add(bean);
        return time;
    }

    /**
     * 使用AOP代理
     * @return
     */
    @RequestMapping("myService")
    public String writeLog(){
        myService.writeLog();
        return "SUCCESS";
    }

    /**
     * 请求中断
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws Exception
     */
    @RequestMapping("/writeData")
    @ResponseBody
    public void writeData(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            int i = 0;
            PrintWriter printWriter = httpServletResponse.getWriter();
            while (true) {
                Thread.sleep(1000);
                i++;
                printWriter.write(i);
                if (i % 10 == 0) {
                    System.out.print(i);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
