package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.context.GPApplicationContext;
import com.gupaoedu.vip.spring.formework.webmvc.GPHandlerAdapter;
import com.gupaoedu.vip.spring.formework.webmvc.GPHandlerMapping;
import com.gupaoedu.vip.spring.formework.webmvc.GPModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 2018/4/21.
 */

//Servlet只是作为一个MVC的启动入口
public class DispatcherServlet extends HttpServlet {


    private  final String LOCATION = "contextConfigLocation";

//    private Map<String,GPHandlerMapping> handlerMapping = new HashMap<String,GPHandlerMapping>();

    //课后再去思考一下这样设计的经典之处
    //GPHandlerMapping最核心的设计，也是最经典的
    //它牛B到直接干掉了Struts、Webwork等MVC框架
    private List<GPHandlerMapping> handlerMappings = new ArrayList<GPHandlerMapping>();

    private List<GPHandlerAdapter> handlerAdapters = new ArrayList<GPHandlerAdapter>();

    @Override
    public void init(ServletConfig config) throws ServletException {

        //相当于把IOC容器初始化了
        GPApplicationContext context = new GPApplicationContext(config.getInitParameter(LOCATION));


        initStrategies(context);

    }


    protected void initStrategies(GPApplicationContext context) {

        //有九种策略
        // 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
        // 每种策略可以自定义干预，但是最终的结果都是一致
        // ModelAndView

        // =============  这里说的就是传说中的九大组件 ================
        initMultipartResolver(context);//文件上传解析，如果请求类型是multipart将通过MultipartResolver进行文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析

        /** 我们自己会实现 */
        //GPHandlerMapping 用来保存Controller中配置的RequestMapping和Method的一个对应关系
        initHandlerMappings(context);//通过HandlerMapping，将请求映射到处理器
        /** 我们自己会实现 */
        //HandlerAdapters 用来动态匹配Method参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配

        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver来解析
        initRequestToViewNameTranslator(context);//直接解析请求到视图名

        /** 我们自己会实现 */
        //通过ViewResolvers实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver解析逻辑视图到具体视图实现

        initFlashMapManager(context);//flash映射管理器
    }

    private void initFlashMapManager(GPApplicationContext context) {}
    private void initRequestToViewNameTranslator(GPApplicationContext context) {}
    private void initHandlerExceptionResolvers(GPApplicationContext context) {}
    private void initThemeResolver(GPApplicationContext context) {}
    private void initLocaleResolver(GPApplicationContext context) {}
    private void initMultipartResolver(GPApplicationContext context) {}




    //将Controller中配置的RequestMapping和Method进行一一对应
    private void initHandlerMappings(GPApplicationContext context) {
        //按照我们通常的理解应该是一个Map
        //Map<String,Method> map;
        //map.put(url,Method)



    }

    private void initHandlerAdapters(GPApplicationContext context) {
    }

    private void initViewResolvers(GPApplicationContext context) {

    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String url = req.getRequestURI();
//        String contextPath = req.getContextPath();
//        url = url.replace(contextPath,"").replaceAll("/+","/");
//        GPHandlerMapping handler = handlerMapping.get(url);

//        try {
//            GPModelAndView mv = (GPModelAndView)handler.getMethod().invoke(handler.getController());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }


        //对象.方法名才能调用
        //对象要从IOC容器中获取
//        method.invoke(context.);

        //doDispatch(req,resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {

        GPHandlerMapping handler = getHandler(req);


        GPHandlerAdapter ha = getHandlerAdapter(handler);


        GPModelAndView mv = ha.handle(req,resp,handler);

        processDispatchResult(resp,mv);

    }

    private void processDispatchResult(HttpServletResponse resp, GPModelAndView mv) {

        //调用viewResolver的resolveView方法
    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        return null;
    }

    private GPHandlerMapping getHandler(HttpServletRequest req) {
        return null;
    }


}
