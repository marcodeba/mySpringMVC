package com.gupaoedu.vip.demo.mvc.action;

import com.gupaoedu.vip.demo.service.IDemoService;
import com.gupaoedu.vip.spring.annotation.Autowried;
import com.gupaoedu.vip.spring.annotation.Controller;
import com.gupaoedu.vip.spring.annotation.RequestMapping;
import com.gupaoedu.vip.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/demo")
public class DemoAction {

    @Autowried
    private IDemoService demoService;

    @RequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @RequestParam("name") String name) {
        String result = demoService.get(name);
        System.out.println(result);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/edit")
    public void edit(HttpServletRequest req, HttpServletResponse resp, Integer id) {

    }
}
