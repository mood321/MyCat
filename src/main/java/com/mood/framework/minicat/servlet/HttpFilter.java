package com.mood.framework.minicat.servlet;

import com.mood.framework.minicat.entity.ApplicationFilterChain;
import com.mood.framework.minicat.entity.HttpServletRequest;
import com.mood.framework.minicat.entity.HttpServletResponse;

import java.io.IOException;

public abstract class HttpFilter extends  HttpPart {
    private String mapping;



    public String getMapping() {
        return mapping;
    }



    public void setMapping(String mapping) {
        this.mapping = mapping;
    }



    public abstract void doFilter(HttpServletRequest request, HttpServletResponse response, ApplicationFilterChain chain) throws IOException;

}
