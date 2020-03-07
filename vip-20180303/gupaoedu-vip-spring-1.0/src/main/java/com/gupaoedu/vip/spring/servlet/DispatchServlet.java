package com.gupaoedu.vip.spring.servlet;

import com.gupaoedu.vip.spring.annotation.Autowried;
import com.gupaoedu.vip.spring.annotation.Controller;
import com.gupaoedu.vip.spring.annotation.RequestMapping;
import com.gupaoedu.vip.spring.annotation.Service;
import com.gupaoedu.vip.spring.handlermapping.HandlerMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private Map<String, Object> iocContainer = new ConcurrentHashMap<String, Object>();

    private List<String> classNames = new ArrayList<String>();

    private List<HandlerMapping> handlerMappingList = new ArrayList<HandlerMapping>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", "\r\n") + "<font color='green'><i>Copyright@GupaoEDU</i></font>");
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping handlerMapping = this.getHandlerMapping(req);
        if (handlerMapping == null) {
            resp.getWriter().write("<font size='25' color='red'>404 Not Found</font><br/><font color='green'><i>Copyright@GupaoEDU</i></font>");
            return;
        }

        //获得方法的形参列表(HttpRequest, HttpResponse, java.lang.String)
        Class<?>[] paramTypes = handlerMapping.getParamTypes();
        //参数的名字作为key,参数的位置作为value,request->0,response->1,name->2
        Map<String, Integer> paramIndexMapping = handlerMapping.getParamIndexMapping();
        Object[] paramValues = new Object[paramTypes.length];

        //实参,name->Tom
        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            if (!paramIndexMapping.containsKey(parm.getKey())) {
                continue;
            }
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            int index = paramIndexMapping.get(parm.getKey());
            paramValues[index] = convert(paramTypes[index], value);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        Object returnValue = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }
        resp.getWriter().write(returnValue.toString());
    }

    private HandlerMapping getHandlerMapping(HttpServletRequest req) {
        if (this.handlerMappingList.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handler : this.handlerMappingList) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    //url传过来的参数都是String类型的，HTTP是基于字符串协议
    //只需要把String转换为任意类型就好
    private Object convert(Class<?> type, String value) {
        //如果是int
        if (Integer.class == type) {
            return Integer.valueOf(value);
        } else if (Double.class == type) {
            return Double.valueOf(value);
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现
        return value;
    }

    @Override
    public void init(ServletConfig config) {
        //加载配置文件(application.properties)
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //加载，加载指定目录下的Bean Name
        doScanner(contextConfig.getProperty("scanPackage"));

        //注册，给每个Bean Name创建一个实例，并注册到一个map中，key就是bean Name，value就是Bean Instance
        doRegistry();

        //自动依赖注入
        //在Spring中是通过调用getBean方法才出发依赖注入的,对Bean Instance注入
        doAutowired();

        //DemoAction action = (DemoAction) iocContainer.get("demoAction");
        //action.query(null, null, "Tom");

        //如果是SpringMVC会多设计一个HnandlerMapping

        //将@RequestMapping中配置的url和一个Method关联上
        //以便于从浏览器获得用户输入的url以后，能够找到具体执行的Method通过反射去调用
        initHandlerMapping();
        System.out.println("DispatcherServlet has been inited");
    }

    private void initHandlerMapping() {
        if (iocContainer.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }

            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappingList.add(new HandlerMapping(pattern, entry.getValue(), method));
                System.out.println("Mapped :" + pattern + "," + method);
            }
        }
    }

    /**
     * map.put(beanName, beanInstance);
     */
    private void doRegistry() {
        if (classNames.isEmpty()) {
            return;
        }

        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);

                //在Spring中用的多个子方法来处理的
                if (clazz.isAnnotationPresent(Controller.class)) {
                    //在Spring中在这个阶段不是不会直接put instance，这里put的是BeanDefinition
                    iocContainer.put(lowerFirstCase(clazz.getSimpleName()), clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    //默认用类名首字母注入
                    //如果自己定义了beanName，那么优先使用自己定义的beanName
                    //如果是一个接口，使用接口的类型去自动注入
                    //在Spring中同样会分别调用不同的方法 autowriedByName autowritedByType
                    String beanName = ("".equals(service.value().trim())) ? this.lowerFirstCase(clazz.getSimpleName())
                            : service.value().trim();

                    Object instance = clazz.newInstance();
                    iocContainer.put(beanName, instance);

                    for (Class<?> i : clazz.getInterfaces()) {
                        if (!iocContainer.containsKey(i.getName())) {
                            iocContainer.put(i.getName(), instance);
                        }
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        if (iocContainer.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowried.class)) {
                    continue;
                }
                Autowried autowried = field.getAnnotation(Autowried.class);
                String beanName = autowried.value();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), iocContainer.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private void doLoadConfig(String location) {
        //在Spring中是通过Reader去查找和定位对不对
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));

        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
