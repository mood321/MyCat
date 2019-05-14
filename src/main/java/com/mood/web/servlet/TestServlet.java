package com.mood.web.servlet;

import com.mood.framework.minicat.annotation.Servlet;
import com.mood.framework.minicat.entity.HttpServletRequest;
import com.mood.framework.minicat.entity.HttpServletResponse;
import com.mood.framework.minicat.servlet.HttpServlet;

import java.io.IOException;

@Servlet(value = "/index.do")
public class TestServlet extends HttpServlet {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name=request.getParament("name");
        response.getOutputStream().write("Hello,I'm "+name);

    }

}
