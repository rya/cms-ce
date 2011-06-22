package com.enonic.cms.admin.direct;

import ch.ralscha.extdirectspring.api.ApiController;
import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.controller.RouterController;
import ch.ralscha.extdirectspring.util.JsonHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/data/direct")
public final class ExtDirectController
    implements InitializingBean
{
    private final ApiController apiController;
    private final RouterController routerController;

    @Autowired
    public ExtDirectController(final ApplicationContext context, final ConversionService conversionService)
    {
        final JsonHandler handler = new JsonHandler();
        this.apiController = new ApiController(context, handler);
        this.routerController = new RouterController(context, conversionService, handler);
    }

    public void afterPropertiesSet()
        throws Exception
    {
        this.routerController.afterPropertiesSet();
    }

    @RequestMapping(value = { "/api.js", "/api-debug.js" }, method = RequestMethod.GET)
    public void api(final HttpServletRequest request, final HttpServletResponse response)
        throws Exception
    {
        this.apiController.api("CMS.direct", "CMS.rpc", "REMOTING_API", "POLLING_URLS", null, false, null, request, response);
    }

    @RequestMapping(value = "/router", method = RequestMethod.POST, params = "!extAction")
    @ResponseBody
    public List<ExtDirectResponse> router(final HttpServletRequest request, final HttpServletResponse response,
                                          final Locale locale, final @RequestBody Object requestData)
    {
        return this.routerController.router(request, response, locale, requestData);
    }

    @RequestMapping(value = "/router", method = RequestMethod.POST, params = "extAction")
    public String router(final @RequestParam("extAction") String extAction,
                         final @RequestParam("extMethod") String extMethod)
    {
        return this.routerController.router(extAction, extMethod);
    }

    @RequestMapping(value = "/poll/{beanName}/{method}/{event}")
    @ResponseBody
    public ExtDirectPollResponse poll(final @PathVariable("beanName") String beanName,
                                      final @PathVariable("method") String method,
                                      final @PathVariable("event") String event,
                                      final HttpServletRequest request, final HttpServletResponse response,
                                      final Locale locale)
        throws Exception
    {
        return this.routerController.poll(beanName, method, event, request, response, locale);
    }
}
