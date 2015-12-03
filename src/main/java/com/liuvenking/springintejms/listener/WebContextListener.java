package com.liuvenking.springintejms.listener;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * Created by venking on 15/11/5.
 */
public class WebContextListener extends ContextLoaderListener {
    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        return super.initWebApplicationContext(servletContext);
    }
}
