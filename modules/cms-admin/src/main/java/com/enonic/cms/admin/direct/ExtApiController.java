package com.enonic.cms.admin.direct;

import ch.ralscha.extdirectspring.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/direct")
public class ExtApiController
    extends ApiController
{
    @Autowired
    public ExtApiController(final ApplicationContext context)
    {
        super(context);
    }
}
