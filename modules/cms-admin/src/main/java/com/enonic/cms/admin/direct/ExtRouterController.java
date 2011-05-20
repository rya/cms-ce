package com.enonic.cms.admin.direct;

import ch.ralscha.extdirectspring.controller.RouterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/direct")
public class ExtRouterController
    extends RouterController
{
    @Autowired
    public ExtRouterController(final ApplicationContext context, final ConversionService conversionService)
    {
        super(context, conversionService);
    }
}
