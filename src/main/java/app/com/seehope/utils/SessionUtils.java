package app.com.seehope.utils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: 龍右
 * @Date: 2020/1/5 21:39
 * @Description: session处理类
 */
public class SessionUtils {

    private static Lock lock = new ReentrantLock();

    private static ConcurrentHashMap sessionIdMap = new ConcurrentHashMap(new HashMap<Object,Object>());

    public static void addSession(HttpSession session) {
        lock.lock();
        if (session != null) {
        	sessionIdMap.put(session.getId(), session);
        }
        lock.unlock();
    }

    public static void delSession(HttpSession session) {
        lock.lock();
        if (session != null) {
        	sessionIdMap.remove(session.getId());
        }
        lock.unlock();
    }

    public static HttpSession getSession(String session_id) {
        lock.lock();
        try{
            if (sessionIdMap.containsKey(session_id)){
                return (HttpSession) sessionIdMap.get(session_id);
            }else{
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
        return null;
    }


}
