package org.apache.jmeter.protocol.web.config;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;

public class WebBrowserCacheConfigBeanInfo extends BeanInfoSupport {
    private static final String CACHE_SETTINGS = "cacheSettings";
    private static final String ITERATION_LOOP_SETTINGS = "iterationLoopSettings";
    static final String DO_NOT_CLEAR = "retain its cookies and cache";
    static final String CLEAR_COOKIES = "clear its cookies";
    static final String CLEAR_ALL = "clear all cookies and cache (creates a new browser)";

    public WebBrowserCacheConfigBeanInfo() {
        super(WebBrowserCacheConfig.class);

        PropertyDescriptor p = null;

        // configuration per iteration (ie. at the start of each loop)
        createPropertyGroup(ITERATION_LOOP_SETTINGS, new String[] {CACHE_SETTINGS});

        // cache configuration
        p = property(CACHE_SETTINGS);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, DO_NOT_CLEAR);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(TAGS, new String[] {DO_NOT_CLEAR, CLEAR_COOKIES, CLEAR_ALL});
    }
}
