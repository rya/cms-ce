package com.enonic.cms.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class AdminController
{
    @RequestMapping(method = RequestMethod.GET)
    public String redirect()
    {
        return "redirect:index.html";
    }
}
