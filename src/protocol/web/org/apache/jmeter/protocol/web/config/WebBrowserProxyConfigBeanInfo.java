package org.apache.jmeter.protocol.web.config;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;

public class WebBrowserProxyConfigBeanInfo extends BeanInfoSupport {
    static final String RUN_SETTINGS = "runSettings";
    static final String PROXY_SETTINGS = "proxySettings";
    static final String PAC_URL = "pacUrl";
    static final String HTTP_PROXY = "httpProxy";
    static final String HTTPS_PROXY = "httpsProxy";
    static final String FTP_PROXY = "ftpProxy";
    static final String PROXY_AUTO_DETECT = "be automatically configured";
    static final String PROXY_DIRECT = "not be used";
    static final String PROXY_PAC = "be configured using a PAC URL (below)";
    static final String PROXY_MANUAL = "be manually configured (below)";
    
    public WebBrowserProxyConfigBeanInfo() {
    	super(WebBrowserProxyConfig.class);

        PropertyDescriptor p = null;

        // configuration per test run (ie. configured only once)
        createPropertyGroup(RUN_SETTINGS, new String[] {PROXY_SETTINGS, PAC_URL, HTTP_PROXY, HTTPS_PROXY, FTP_PROXY});

        // proxy configuration
        p = property(PROXY_SETTINGS);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, PROXY_AUTO_DETECT);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(TAGS, new String[] {PROXY_AUTO_DETECT, PROXY_DIRECT, PROXY_PAC, PROXY_MANUAL});
        p = property(PAC_URL);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p = property(HTTP_PROXY);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p = property(HTTPS_PROXY);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p = property(FTP_PROXY);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
    }
}
