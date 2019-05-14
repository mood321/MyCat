package com.mood.framework.minicat.container;

import com.mood.framework.minicat.catpool.CatThreadPool;
import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.framework.minicat.entity.HttpSession;
import com.mood.framework.minicat.util.EncryptUtil;
import com.mood.framework.minicat.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionContainer {
    public static final Map<String, HttpSession> SYSTEM_SESSION_CONTAINER = new ConcurrentHashMap<>();
    private static final  long startTime=System.currentTimeMillis();
    static {
        //线程处理session过期
        CatThreadPool.SESSION_POOL.execute(() -> {
            sessionGuard();
        });
    }

    private static void sessionGuard() {
        while (true) {

            try {
                if(StringUtil.isNullOrEmpty(SYSTEM_SESSION_CONTAINER)){
                    return;
                }
                List<String> willCleanSessionIds=new ArrayList<String>();
                for(String key:SYSTEM_SESSION_CONTAINER.keySet()){
                    HttpSession session=SYSTEM_SESSION_CONTAINER.get(key);
                    if(System.currentTimeMillis()-session.getActiveTime().getTime()> MoodCatConfig.SESSION_TIMEOUT){
                        willCleanSessionIds.add(key);
                    }
                }
                for(String key:willCleanSessionIds){
                    SYSTEM_SESSION_CONTAINER.remove(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    TimeUnit.SECONDS.sleep(1l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean containsSession(String sessionId) {
        return SYSTEM_SESSION_CONTAINER.containsKey(sessionId);
    }

    public static HttpSession getSession(String sessionId) {
        if(StringUtil.isNullOrEmpty(sessionId)){
            return null;
        }
        HttpSession session=SYSTEM_SESSION_CONTAINER.get(sessionId);
        if(session==null){
            return session;
        }
        session.setActiveTime(new Date());
        return session;
    }

    public static HttpSession setSession(String sessionId, HttpSession session) {
        return SYSTEM_SESSION_CONTAINER.put(sessionId, session);
    }

    public static HttpSession initSession(String sessionId) {
        if (SYSTEM_SESSION_CONTAINER.containsKey(sessionId)) {
            return SYSTEM_SESSION_CONTAINER.get(sessionId);
        }
        HttpSession session = new HttpSession();
        SYSTEM_SESSION_CONTAINER.put(sessionId, session);
        return session;
    }


    private static int sessionIndex = 0;

    public static String createSessionId() {
        Integer currentSessionIndex = 0;
        synchronized (SessionContainer.class) {
            sessionIndex++;
            currentSessionIndex = sessionIndex;
        }
        String key = startTime + currentSessionIndex.toString();
        return EncryptUtil.md5Code(key);
    }
}
