package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.protocol.web.util.BrowserType;
import org.apache.jmeter.testbeans.BeanInfoSupport;

import java.beans.PropertyDescriptor;

public class WebBrowserTypeConfigBeanInfo extends BeanInfoSupport {
    private static final String TYPE = "type";
    private static final String BROWSER_TYPE_SETTINGS = "browserTypeSettings";
    static final String FIREFOX = BrowserType.FIREFOX.name();
    static final String CHROME = BrowserType.CHROME.name();

    public WebBrowserTypeConfigBeanInfo() {
        super(WebBrowserTypeConfig.class);

        PropertyDescriptor p = null;

        // configuration per iteration (ie. at the start of each loop)
        createPropertyGroup(BROWSER_TYPE_SETTINGS, new String[] {TYPE});

        // cache configuration
        p = property(TYPE);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, FIREFOX);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(TAGS, new String[] {FIREFOX, CHROME});
    }
}
