package app.com.seehope.config;

import app.com.seehope.utils.SessionUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @Author: 龍右
 * @Date: 2020/1/5 22:47
 * @Description:
 */
@Component
public class SessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        SessionUtils.addSession(httpSessionEvent.getSession());
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        SessionUtils.delSession(session);
    }
}
