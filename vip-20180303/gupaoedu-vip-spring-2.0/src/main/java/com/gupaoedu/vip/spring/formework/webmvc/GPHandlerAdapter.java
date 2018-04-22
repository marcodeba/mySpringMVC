package com.gupaoedu.vip.spring.formework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tom on 2018/4/22.
 */
//专人干专事
public class GPHandlerAdapter {


    /**
     *
     * @param req
     * @param resp
     * @param handler 为什么要把handler传进来
     *                因为handler中包含了controller、method、url信息
     * @return
     */
    public GPModelAndView handle(HttpServletRequest req, HttpServletResponse resp, GPHandlerMapping handler) {
        //根据用户请求的参数信息，跟method中的参数信息进行动态匹配
        //resp 传进来的目的只有一个：只是为了将其赋值给方法参数，仅此而已

        //只有当用户传过来的ModelAndView为空的时候，才会new一个默认的
        return null;
    }
}
