package com.tofftran.mockproject.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("route")
public class RouteUtils {

    //To enable active item for Spring 3. Used in navbar items
    public boolean isActive(String path, boolean exact) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        HttpServletRequest request = attributes.getRequest();

        if (exact) {
            return path.equals(request.getRequestURI());
        }
        return request.getRequestURI().contains(path);
    }

    public boolean isActive(String path) {
        return isActive(path, false);
    }
}
