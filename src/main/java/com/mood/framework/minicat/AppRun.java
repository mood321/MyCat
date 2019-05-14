package com.mood.framework.minicat;

import com.mood.framework.minicat.annotation.Filter;
import com.mood.framework.minicat.annotation.Servlet;
import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.framework.minicat.container.FilterContainer;
import com.mood.framework.minicat.container.ServletContainer;
import com.mood.framework.minicat.service.BIOService;
import com.mood.framework.minicat.service.NIOService;
import com.mood.framework.minicat.service.serviceInterface.Service;
import com.mood.framework.minicat.servlet.HttpFilter;
import com.mood.framework.minicat.servlet.HttpPart;
import com.mood.framework.minicat.servlet.HttpServlet;
import com.mood.framework.minicat.util.StringUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public class AppRun {
    static Service service = null;

    public static void init(Class... classes) {
        long start = System.currentTimeMillis();

        //开启模式
        if (MoodCatConfig.MODEL == 1) {
            service = new BIOService();
        }
        if (MoodCatConfig.MODEL == 2) {
            service = new NIOService();
        }
        System.out.println("service 模式====>" + service.getClass().getClass());

        try {
            //加载配置
            for (Class clazz : classes) {
                if (!HttpPart.class.isAssignableFrom(clazz)) {
                    continue;
                }

                Servlet servletFlag = (Servlet) clazz.getAnnotation(Servlet.class);
                if (servletFlag != null && !StringUtil.isNullOrEmpty(servletFlag.value())) {
                    HttpServlet servlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
                    System.out.println("注册Servlet>>" + clazz.getName() + ">>" + servletFlag.value());
                    ServletContainer.pushServlet(servletFlag.value(), servlet);
                }
                Filter filterFlag = (Filter) clazz.getAnnotation(Filter.class);
                if (filterFlag != null && !StringUtil.isNullOrEmpty(filterFlag.value())) {
                    HttpFilter filter = (HttpFilter) clazz.getDeclaredConstructor().newInstance();
                    filter.setMapping(filterFlag.value());
                    System.out.println("注册Filter>>" + clazz.getName() + ">>" + filterFlag.value());
                    FilterContainer.pushFilter(filter);
                }
            }
            service.openProt(MoodCatConfig.HTTP_PORT, MoodCatConfig.SESSION_TIMEOUT);
            service.doService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
