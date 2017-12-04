package com.gupaoedu.controller;

import javax.core.common.doc.annotation.Api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/open")
public class RegController {

	
	@Api(name="用户登录",
		desc="",
		author="Tom",createtime="2016-12-04")
	@RequestMapping(value="/login.json")
	public void login() {
	}
	
}
