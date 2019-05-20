package com.mood.framework.minicat.press;


import com.mood.framework.minicat.init.abs.HttpBulider;
import com.mood.framework.minicat.catpool.CatThreadPool;
import com.mood.framework.minicat.container.ServletContainer;
import com.mood.framework.minicat.entity.ApplicationFilterChain;
import com.mood.framework.minicat.servlet.HttpServlet;

public class MoodcatProcess {


	public static void doService(HttpBulider build) throws 	Exception {
			HttpServlet servlet = ServletContainer.getServlet(build.getRequest().getRequestURI());
			ApplicationFilterChain chain=new ApplicationFilterChain(servlet);
			chain.doFilter(build.getRequest(), build.getResponse());
		
	}

	static {
		CatThreadPool.SESSION_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}
}
