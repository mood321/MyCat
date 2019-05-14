package com.mood.framework.minicat.catpool;

import com.mood.framework.minicat.config.MoodCatConfig;

import java.util.concurrent.*;

public class CatThreadPool {
    /**
     * http 连接线程池
     */
  public static final ExecutorService HTTP_POOL=new ThreadPoolExecutor(100, MoodCatConfig.MINICAT_THREAD_NUM,
            10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>() );

    /**
     * session 连接池
     */
    public static final ExecutorService SESSION_POOL=new ThreadPoolExecutor(100, MoodCatConfig.MINICAT_THREAD_NUM,
            10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>() );
}
