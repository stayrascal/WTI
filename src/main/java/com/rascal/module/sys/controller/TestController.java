package com.rascal.module.sys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Date: 2015/11/30
 * Time: 14:05
 *
 * @author Rascal
 */
@Controller
public class TestController {

    @RequestMapping(value = "/test/index", method = RequestMethod.GET)
    public String index() {
        return "test/index";
    }
}
