package com.mood.framework.minicat.container;

import com.mood.framework.minicat.servlet.HttpServlet;
import com.mood.framework.minicat.util.AntUtil;

import java.util.HashMap;
import java.util.Map;

public class ServletContainer {
 public  static  final Map<String, HttpServlet> SERVLET_CONTAINER=new HashMap<>();

 public static void pushServlet(String path,HttpServlet servlet){
     SERVLET_CONTAINER.put(path,servlet);
 }

    public static HttpServlet getServlet(String path){
        HttpServlet servlet=SERVLET_CONTAINER.get(path);
        if(servlet!=null){
            return servlet;
        }
        for(String patt:SERVLET_CONTAINER.keySet()){
            if(AntUtil.isAntMatch(path, patt)){
                return SERVLET_CONTAINER.get(patt);
            }
        }
        return null;
    }

}
