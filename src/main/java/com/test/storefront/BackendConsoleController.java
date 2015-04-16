/*
 * nemesis Platform - NExt-generation Multichannel E-commerce SYStem
 *
 * Copyright (c) 2010 - 2013 nemesis
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of nemesis
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with nemesis.
 */
package com.test.storefront;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class BackendConsoleController {

    @Resource(name = "restBaseUrl")
    private String restBaseUrl;

    @RequestMapping(value = { "/", "/console" }, method = RequestMethod.GET)
    public String home(final Model model, final HttpServletRequest request) {
        model.addAttribute("restBaseUrl", restBaseUrl);
        return "index";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public String login() {
        return "login";
    }
}
