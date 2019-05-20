package com.mood.framework.minicat.entity;


import com.mood.framework.minicat.exception.PageNotFoundException;
import com.mood.framework.minicat.container.FilterContainer;
import com.mood.framework.minicat.servlet.HttpFilter;
import com.mood.framework.minicat.servlet.HttpServlet;
import com.mood.framework.minicat.util.AntUtil;

import java.io.IOException;

public final class ApplicationFilterChain {
	
	public ApplicationFilterChain(HttpServlet servlet){
		this.servlet=servlet;
	}

	private int pos = 0;
	private HttpServlet servlet;

	public void doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (pos < FilterContainer.FILTER_CONTAINER.size()) {
			HttpFilter filter = FilterContainer.FILTER_CONTAINER.get(pos++);
			if(!AntUtil.isAntMatch(request.getRequestURI(), filter.getMapping())){
				doFilter(request, response);
				return;
			}
			filter.doFilter(request, response, this);
			return;
		}
		if (servlet == null) {
			throw new PageNotFoundException("该页面未找到>>" + request.getRequestURI());
		}
		servlet.doService(request, response);
	}

}