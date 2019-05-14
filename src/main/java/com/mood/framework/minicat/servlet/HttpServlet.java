package com.mood.framework.minicat.servlet;

import com.mood.framework.minicat.entity.HttpServletRequest;
import com.mood.framework.minicat.entity.HttpServletResponse;

import java.io.IOException;

public abstract class HttpServlet extends  HttpPart {

    public abstract void doService(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
