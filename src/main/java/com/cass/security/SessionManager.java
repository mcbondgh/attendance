package com.cass.security;

import com.vaadin.flow.server.VaadinSession;

public class SessionManager {
    private static String SESSION_KEY = "activeUser";

    public static void setUsername(Object value) {
        VaadinSession.getCurrent().setAttribute(SESSION_KEY, value);
    }

public static void setAttribute(String key, Object value) {
        VaadinSession.getCurrent().setAttribute(key, value);
    }

    public static Object getAttribute(String key) {
        return VaadinSession.getCurrent().getAttribute(key);
    }

    public static void destroySession() {
        VaadinSession.getCurrent().getSession().invalidate();
    }
}
